package com.glyph.scala.game.puzzle.model.monsters

import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.game.puzzle.system.TurnProcessor
import com.glyph.scala.game.puzzle.model.match_puzzle.Panel

/**
 * @author glyph
 */
trait Monster extends Panel{
  val hp = Var(100)

  /**
   * Monster matches to monster
   * @param other:Panel
   * @return
   */
  def matchTo(other: Panel): Boolean = other match {
    case o:Monster => true
    case _ => false
  }
  def atk:Int
}
object Monster{
  val constructors = Array(()=>new Slime)
  def random():Monster = constructors((Math.random()*constructors.length-1).toInt)()
}