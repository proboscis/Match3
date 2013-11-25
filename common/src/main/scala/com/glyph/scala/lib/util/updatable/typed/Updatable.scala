package com.glyph.scala.lib.util.updatable.typed

import scalaz._
import Scalaz._

/**
 * @author glyph
 */
trait Updatable[T] {
  def update(tgt:T)(delta:Float)
}
trait Task[T] extends Updatable[T]{
  def isCompleted(tgt:T):Boolean
  def onStart(tgt:T)
  def onFinish(tgt:T)
}
class Processor