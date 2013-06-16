package com.glyph.scala.game.model.cardgame

/**
 * @author glyph
 */
class Battle {
  val enemies = collection.mutable.ListBuffer[GameCharacter]()
  val allies = collection.mutable.ListBuffer[GameCharacter]()
}
