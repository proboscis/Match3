package com.glyph.scala.game.model.cardgame

import com.glyph.scala.lib.util.tile.JsonMapParser
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
class StageData {
  val ground = new JsonMapParser().parse(Gdx.files.internal("map/glassGround.json").readString()).layers.head
  val wall = new JsonMapParser().parse(Gdx.files.internal("map/woodwall.json").readString()).layers.head
}
