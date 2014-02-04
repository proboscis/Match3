package com.glyph.scala.lib.util.updatable.task

import com.glyph.scala.game.Glyphs

/**
 * @author glyph
 */
class Delay extends TimedTask
object Delay{
  import com.glyph.scala.lib.util.pool.GlobalPool._
  import Glyphs._
  def apply(d:Float):Delay= auto[Delay] in d
}
