package com.glyph.make3

import _root_.scala.collection.mutable
import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph._scala.lib.util.{Threading, Logging}
import com.glyph._scala.test.TestRunner
import javax.microedition.khronos.egl._
import com.glyph._scala.lib.injection.{DefaultGLExecutionContext, GLExecutionContext}
import android.graphics.SurfaceTexture
import com.glyph.play.{SocialService, GameService, PlayManagerActivity}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.Gdx
import scala.util.Try

class Main
  extends AndroidApplication
  with Logging
  with PlayManagerActivity
  with GameService
  with SocialService {
  var loader:LoaderContext = null
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true

    new Thread(Thread.currentThread().getThreadGroup, new Runnable {
      def run() {

        initialize(new TestRunner("")(()=>{
          try{
            loader = new LoaderContext
            loader.start()
            GLExecutionContext.context = loader
          } catch {
            case t:Throwable => {
              errE("there was an exception while initializing GLWorkerThread, switching to blocking GLContext")(t)
              GLExecutionContext.context = new DefaultGLExecutionContext
            }
          }
        }), config)
      }
    }, Thread.currentThread().getName + "logic", 64000000).run()
    //TODO start activity called from non activity context.
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    loader.safeStop()
  }
}
class GLWorkerThreadException(msg:String) extends RuntimeException(msg)

trait GLWorkerThread extends Thread with Logging{
  val EGL_CONTEXT_CLIENT_VERSION = 0x3098

  def onGLContext()

  var egl:EGL10 = null
  var context:EGLContext = null
  var display:EGLDisplay = null
  var config:EGLConfig = null
  override def run(): Unit = {
    super.run()
    val loadingContext = egl.eglCreateContext(
      display,
      config,
      context,
      Array(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE))
    checkEGLError()
    val textureSurface = new SurfaceTexture(1) //dummy surface for loading
    //this only works on android 3.0 or later
    val surface = egl.eglCreateWindowSurface(display, config, textureSurface, null)
    checkEGLError()
    egl.eglMakeCurrent(display, surface, surface, loadingContext)
    checkEGLError()
    onGLContext()
    egl.eglDestroyContext(display,loadingContext)
    checkEGLError()
  }
  def checkEGLError(){
    val error = egl.eglGetError()
    if(error != EGL10.EGL_SUCCESS) err("warning:ignoring unchecked EGL error code:"+error)
  }
  def assertEGL(egl:EGL10){
    val error = egl.eglGetError()
    if(error != EGL10.EGL_SUCCESS) throw new RuntimeException("egl error while initializing GLWorkerThread.\n error code:"+error)
  }
  def checkGLError(){
    val error = Gdx.gl20.glGetError()
    if(error != GL20.GL_NO_ERROR){
      err("warning: unchecked gl error code:"+error)
    }
  }
  /**
   * you must call this method on GL thread.
   */
  override def start(): Unit ={
    log("creating context")
    egl= EGLContext.getEGL.asInstanceOf[EGL10]
    checkEGLError()
    context = egl.eglGetCurrentContext()
    assertEGL(egl)
    display = egl.eglGetCurrentDisplay()
    assertEGL(egl)
    config = {
      val MAX_CONFIG = 10
      val nConfig = new Array[Int](1)
      egl.eglGetConfigs(display, null, MAX_CONFIG, nConfig)
      assertEGL(egl)
      val numberOfConfigs = nConfig(0)
      val configs = new Array[EGLConfig](numberOfConfigs)
      egl.eglChooseConfig(display, Array(EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, EGL10.EGL_NONE), configs, MAX_CONFIG, nConfig)
      assertEGL(egl)
      configs(0)
    }
    super.start()
  }
}

trait GLQueuedThread extends GLWorkerThread with Logging {
  @volatile var stopped = false
  val queue = new java.util.concurrent.LinkedBlockingQueue[Runnable]
  override def onGLContext(): Unit = {
    while(!stopped){
      try{
        log("waiting for a next task to run")
        queue.take().run()
        checkGLError()
        log("finished a GL task")
      } catch {
        case e:InterruptedException => err("interrupted. continue processing unless safeStop() is called")
      }
    }
  }

  def safeStop() {
    log("stopping gl worker thread.")
    stopped = true
    interrupt()
  }
}

class LoaderContext extends GLQueuedThread with GLExecutionContext {
  override def execute(runnable: Runnable): Unit = {
    log("querying task")
    queue.put(runnable)
  }
  override def reportFailure(t: Throwable): Unit = errE("exception in Android's GLResource Loader:")(t)
}
