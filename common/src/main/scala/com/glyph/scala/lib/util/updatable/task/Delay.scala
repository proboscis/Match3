package com.glyph.scala.lib.util.updatable.task

/**
 * @author glyph
 */
class Delay extends TimedTask
object Delay{
  def apply(d:Float):Delay={
    new Delay in d
  }
}
