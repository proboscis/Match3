package com.glyph.scala.game.puzzle.model.monsters

import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.game.puzzle.model.match_puzzle.{Match3, MaybeDestroyed, DestroyEffect, OnMatch}
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import Match3._
/**
 * @author glyph
 */
trait Monster extends MonsterLike with DestroyEffect with MaybeDestroyed{
  val hp = Var(100,"Monster:hp")
  def isDestroyed: Boolean = hp() <= 0
  val atk = Var(1)
  val exp = Var(1)

  def onDestroy(controller: PuzzleGameController) {
    controller.addExperience(exp())
  }
}
class Weapon extends MonsterLike with OnMatch{
  val atk = Var(50)

  def onMatch(matched: Seq[Panel]) {
    matched foreach {
      case m:Monster => m.hp() -= atk()
      case _=>
    }
  }
}
trait MonsterLike extends Panel{
  def matchTo(other: Panel): Boolean = other.isInstanceOf[MonsterLike]
}
object Monster{
  val constructors = Array(()=>new Slime)
  def random():Monster = constructors((Math.random()*constructors.length-1).toInt)()
}