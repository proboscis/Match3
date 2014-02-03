package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.game.Glyphs

/**
 * @author glyph
 */
class Delay extends TimedTask
object Delay{
  import com.glyph.scala.lib.util.pool.GlobalPool._
  implicit val poolingDelay = Glyphs.genPooling[Delay]
  def apply(d:Float):Delay= globals(classOf[Delay]).auto in d
}
