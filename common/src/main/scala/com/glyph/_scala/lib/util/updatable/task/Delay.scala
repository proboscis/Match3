package com.glyph._scala.lib.util.updatable.task

import com.glyph._scala.game.Glyphs

/**
 * @author glyph
 */
class Delay extends TimedTask
object Delay{
  import com.glyph._scala.lib.util.pool.GlobalPool._
  import Glyphs._
  def apply(d:Float):Delay= auto[Delay] in d
}
