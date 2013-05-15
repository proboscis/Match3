package com.glyph.scala.game.model


/**
 * @author glyph
 */
class Battle {
  val enemies = collection.mutable.ListBuffer[GameCharacter]()
  val allies = collection.mutable.ListBuffer[GameCharacter]()
}
