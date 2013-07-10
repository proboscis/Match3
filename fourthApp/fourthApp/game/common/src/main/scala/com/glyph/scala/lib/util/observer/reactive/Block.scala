package com.glyph.scala.lib.util.observer.reactive

/**
 * @author glyph
 */
class Block[T](block: => T) extends Varying[T] with Reactor {
  Varying.getDependency(block) foreach {
    react(_) {
      _ => notifyObservers(block)
    }
  }

  def current: T = block
}

object Block {
  def apply[T](block: => T): Block[T] = {
    new Block(block)
  }
}
