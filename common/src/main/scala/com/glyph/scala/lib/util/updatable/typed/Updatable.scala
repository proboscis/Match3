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
trait Updater{

  def add[T:Updatable]
}

class Holder(var a:Any, var b:Any){
  def update(delta:Float){

  }
}

object Updater{
}