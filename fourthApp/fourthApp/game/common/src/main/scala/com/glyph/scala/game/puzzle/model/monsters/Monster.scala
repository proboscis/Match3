package com.glyph.scala.game.puzzle.model.monsters

import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.game.puzzle.system.TurnProcessor
import com.glyph.scala.game.puzzle.model.puzzle.Panel

/**
 * @author glyph
 */
trait Monster extends TurnProcessor with Panel{
  val hp = Var(100)

  /**
   * Monster never matches to anything
   * @param other
   * @return
   */
  def matchTo(other: Panel): Boolean = false
}
object Monster{
  val constructors = Array(()=>new Slime)
  def random():Monster = constructors((Math.random()*constructors.length-1).toInt)()
}