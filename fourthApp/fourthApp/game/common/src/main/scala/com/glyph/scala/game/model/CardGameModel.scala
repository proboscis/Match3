package com.glyph.scala.game.model

/**
 * Game model
 * @author glyph
 */
class CardGameModel {
  val stage = new StageData
  val player = new Player
  var battle:Battle = null
}

