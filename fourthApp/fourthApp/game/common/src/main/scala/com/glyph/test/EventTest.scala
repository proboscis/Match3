package com.glyph.test

import com.glyph.scala.lib.util.observer.reactive.EventSource

/**
 * @author glyph
 */
object EventTest {
  def main(args: Array[String]) {
    val event = new EventSource[Int]
    val text = new EventSource[String]
    event.emit(3)
    event.emit(4)
    event.emit(5)
  }
}