package com.glyph.scala.lib.util

/**
 * @author glyph
 */
class MemoryAnalyzer {
  println("start Memory Analyzer")
  new Thread(new Runnable() {
    def run() {
      while (true) {
        Thread.sleep(500)
        //System.gc()
        val runtime = Runtime.getRuntime
        println("total heap: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1000) + "kb")
      }
    }
  }).start()
}
