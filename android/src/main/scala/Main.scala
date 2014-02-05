package com.glyph

import _root_.scala.collection.mutable
import android.os.Bundle
import com.badlogic.gdx.backends.android._
import com.glyph._scala.lib.util.{Threading, Logging}
import com.glyph._scala.test.TestRunner
import javax.microedition.khronos.egl.{EGL, EGLDisplay, EGL10, EGLContext}
import com.badlogic.gdx.backends.android.surfaceview.{GdxEglConfigChooser, GLSurfaceView20}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.lib.injection.{DefaultGLExecutionContext, GLExecutionContext}
import com.google.inject.AbstractModule
import com.glyph._scala.lib.libgdx.GLFuture

class Main extends AndroidApplication with Logging {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val config = new AndroidApplicationConfiguration()
    config.useAccelerometer = true
    config.useCompass = false
    config.useWakelock = true
    config.useGL20 = true
    new Thread(Thread.currentThread().getThreadGroup, new Runnable {
      def run() {
        initialize(new TestRunner, config)
        postRunnable(new Runnable{
          override def run(): Unit ={
            LoaderContext.initialize()
          }
        })
      }
    }, Thread.currentThread().getName + "logic", 64000000).run()
  }
}

class LoaderContext extends GLExecutionContext{
  override def execute(runnable: Runnable){
    LoaderContext.queue.enqueue(runnable)
    LoaderContext.queue.notify()
  }
  override def reportFailure(t: Throwable): Unit = t.printStackTrace()
}

object LoaderContext extends Logging with Threading{
  //I think i gotta handle the context loss too.
  val queue = new mutable.SynchronizedQueue[Runnable]
  def initialize(){
    val egl = EGLContext.getEGL.asInstanceOf[EGL10]
    val context = egl.eglGetCurrentContext()
    val display = egl.eglGetCurrentDisplay()
    //assuming the context is already created
    val config = Gdx.graphics.asInstanceOf[AndroidGraphics].getView.asInstanceOf[GLSurfaceView20].getSelectedConfig
    val loadingContext = egl.eglCreateContext(display,config,context,null)

    new Thread{
      override def run(): Unit = {
        super.run()
        log("create context")
        err(egl.eglGetError())
        val pBufAttrs = Array( EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1,EGL10.EGL_NONE)
        log("create pbuffer surface")
        egl.eglCreatePbufferSurface(display,config,pBufAttrs)
        err(egl.eglGetError())
        while(true){
          while(queue.isEmpty)queue.wait()
          while(!queue.isEmpty){
            queue.dequeue().run()
          }
        }
      }
    }.start()
  }
}