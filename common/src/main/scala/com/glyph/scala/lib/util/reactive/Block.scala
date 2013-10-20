package com.glyph.scala.lib.util.reactive
/*
/**
 * @author glyph
 */
@Deprecated
class Block[T](block: => T) extends Varying[T] with Reactor {

  Varying.getDependency(block) foreach {
    reactVar(_) {
      case _ => notifyObservers(block)
    }
  }

  def current: T = block
}
@Deprecated
object Block {
  def apply[T](block: => T): Block[T] = {
    new Block(block)
  }
}
*/