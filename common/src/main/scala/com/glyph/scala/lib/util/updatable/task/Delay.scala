package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
abstract class Delay extends TimedTask{}
object Delay{
  def apply(d:Float):Delay={
    new Delay {
      var duration: Float = d
    }
  }
  def apply():Delay={
    new Delay {
      var duration: Float = 0
    }
  }
}
