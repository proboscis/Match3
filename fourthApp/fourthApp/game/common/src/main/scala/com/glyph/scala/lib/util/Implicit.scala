package com.glyph.scala.lib.util

/**
 * @author glyph
 */
object Implicit {
  implicit def funcToRunnable(f:()=>Unit):Runnable={
    new Runnable {
      def run() {
        f()
      }
    }
  }
  implicit def blockToRunnable(f: => Unit):Runnable={
    new Runnable {
      def run() {
        f
      }
    }
  }
}
