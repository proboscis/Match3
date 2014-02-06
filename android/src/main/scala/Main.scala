package com.glyph

import _root_.scala.collection.mutable
import android.os.{Build, Bundle}
import com.badlogic.gdx.backends.android._
import com.glyph._scala.lib.util.{Threading, Logging}
import com.glyph._scala.test.TestRunner
import javax.microedition.khronos.egl.{EGL, EGLConfig, EGL10, EGLContext}
import com.badlogic.gdx.backends.android.surfaceview.GLSurfaceView20
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.injection.GLExecutionContext
import scala.util.Try
import com.badlogic.gdx.graphics.Pixmap
import android.graphics.{SurfaceTexture, Bitmap}
import android.graphics.Bitmap.Config

class Main extends AndroidApplication with Logging {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true
    GLExecutionContext.context = new LoaderContext
    new Thread(Thread.currentThread().getThreadGroup, new Runnable {
      def run() {
        initialize(new TestRunner, config)
        postRunnable(new Runnable {
          override def run(): Unit = {
            LoaderContext.initialize()
          }
        })
      }
    }, Thread.currentThread().getName + "logic", 64000000).run()
  }
}

class LoaderContext extends GLExecutionContext {
  override def execute(runnable: Runnable) {
    LoaderContext.queue.synchronized{
      LoaderContext.queue.enqueue(runnable)
      LoaderContext.queue.notify()
    }
  }

  override def reportFailure(t: Throwable): Unit = t.printStackTrace()
}

object LoaderContext extends Logging with Threading {
  //I think i gotta handle the context loss too.
  val queue = new mutable.SynchronizedQueue[Runnable]()
  val EGL_CONTEXT_CLIENT_VERSION = 0x3098

  def initialize() {
    val egl = EGLContext.getEGL.asInstanceOf[EGL10]
    val context = egl.eglGetCurrentContext()
    val display = egl.eglGetCurrentDisplay()
    //assuming the context is already created
    val config = Gdx.graphics.asInstanceOf[AndroidGraphics].getView.asInstanceOf[GLSurfaceView20].getSelectedConfig
    val MAX_CONFIG = 10
    val nConfig = new Array[Int](1)
    egl.eglGetConfigs(display,null,MAX_CONFIG,nConfig)
    val numberOfConfigs = nConfig(0)
    val configs = new Array[EGLConfig](numberOfConfigs)
    egl.eglChooseConfig(display,Array(EGL10.EGL_SURFACE_TYPE,EGL10.EGL_WINDOW_BIT,EGL10.EGL_NONE),configs,MAX_CONFIG,nConfig)
    new Thread {
      override def run(): Unit = {
        super.run()
        log("create context")
        try{
          val loadingContext = egl.eglCreateContext(display, configs(0), context, Array(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE))
          val textureSurface = new SurfaceTexture(1)
          val surface = egl.eglCreateWindowSurface(display,configs(0),textureSurface,null)
          egl.eglMakeCurrent(display,surface,surface,loadingContext)
          log("start loading")
          while (true) {
            queue.synchronized{
              while (queue.isEmpty){
                log("waiting for tasks")
                queue.wait()
              }
              log("processing a task")
              queue.dequeue().run()
              log("done.")
            }
          }
          //egl.eglCreateContext(display, config, context, Array(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE))
        } catch{
          case e:Throwable => errE("context error")(e)
        }
        err("error code:"+egl.eglGetError())
        /*
        val pBufAttrs = Array(EGL10.EGL_WIDTH, 128, EGL10.EGL_HEIGHT, 128, EGL10.EGL_NONE)
        log("create pbuffer surface")
        try{
          egl.eglCreateWindowSurface(display,config,)
          egl.eglCreatePbufferSurface(display, config, pBufAttrs)
        } catch{
          case e:Throwable => {
            errE("pBuffer error")(e)
            err(Option(e.getCause))
            err(Option(e.getLocalizedMessage))
            err(Option(e.getMessage))
            err(e.getStackTrace)
          }
        }
        err("pBuffer error:"+egl.eglGetError())
        */

      }
    }.start()
  }
}