package com.glyph.make3

import _root_.scala.collection.mutable
import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph._scala.lib.util.{Threading, Logging}
import com.glyph._scala.test.TestRunner
import javax.microedition.khronos.egl._
import com.glyph._scala.lib.injection.GLExecutionContext
import android.graphics.SurfaceTexture
import com.glyph.play.{SocialService, GameService, PlayManagerActivity}

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
        loader = new LoaderContext {}
        GLExecutionContext.context = loader
        initialize(new TestRunner, config)
        postRunnable(new Runnable {
          override def run(): Unit = {
            loader.start()
          }
        })
      }
    }, Thread.currentThread().getName + "logic", 64000000).run()
    //TODO start activity called from non activity context.
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    loader.safeStop()
  }
}

trait GLWorkerThread extends Thread {
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
    val textureSurface = new SurfaceTexture(1) //dummy surface for loading
    //this only works on android 3.0 or later
    val surface = egl.eglCreateWindowSurface(display, config, textureSurface, null)
    egl.eglMakeCurrent(display, surface, surface, loadingContext)
    onGLContext()
    egl.eglDestroyContext(display,loadingContext)
  }

  /**
   * you must call this method on GL thread.
   */
  override def start(): Unit ={
    egl= EGLContext.getEGL.asInstanceOf[EGL10]
    context = egl.eglGetCurrentContext()
    display = egl.eglGetCurrentDisplay()
    config = {
      val MAX_CONFIG = 10
      val nConfig = new Array[Int](1)
      egl.eglGetConfigs(display, null, MAX_CONFIG, nConfig)
      val numberOfConfigs = nConfig(0)
      val configs = new Array[EGLConfig](numberOfConfigs)
      egl.eglChooseConfig(display, Array(EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, EGL10.EGL_NONE), configs, MAX_CONFIG, nConfig)
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
        queue.take().run()
      } catch {
        case e:InterruptedException => log("interrupted. continue processing unless safeStop() is called")
      }
    }
  }

  def safeStop() {
    stopped = true
    interrupt()
  }
}

trait LoaderContext extends GLQueuedThread with GLExecutionContext {
  override def execute(runnable: Runnable): Unit =  queue.put(runnable)
  override def reportFailure(t: Throwable): Unit = errE("exception in Android's GLResource Loader:",t)
}
