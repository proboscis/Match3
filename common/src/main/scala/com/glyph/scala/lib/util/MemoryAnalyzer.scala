package com.glyph.scala.lib.util

import com.badlogic.gdx.{LifecycleListener, Gdx}

/**
 * @author glyph
 */
class MemoryAnalyzer {
  var running = true
  val runner = new Runnable() {
    def run() {
      println("start Memory Analyzer")
      while (running) {
        Thread.sleep(500)
        val runtime = Runtime.getRuntime
        println("total heap: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1000) + "kb")
      }
    }
  }
  new Thread(runner).start()
  Gdx.app.addLifecycleListener(new LifecycleListener {
    def dispose(){
      running = false
    }

    def pause(){
      running = false
    }

    def resume(){
      running = true
      new Thread(runner).start()
    }
  })
}
