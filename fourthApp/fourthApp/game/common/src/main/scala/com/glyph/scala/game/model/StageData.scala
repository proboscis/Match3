package com.glyph.scala.game.model

import com.glyph.scala.lib.util.tile.JsonMapParser
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.util.tile.model.Layer

/**
 * @author glyph
 */
class StageData {
  /*
  val map = Seq(
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 0, 0, 0, 0, 0, 0, 1,
    1, 0, 0, 0, 0, 0, 0, 1,
    1, 0, 0, 0, 0, 0, 0, 1,
    1, 0, 0, 0, 0, 0, 0, 1,
    1, 0, 0, 0, 0, 0, 0, 1,
    1, 0, 0, 0, 0, 0, 0, 1,
    1, 1, 1, 1, 1, 1, 1, 1
  )
  */
  val ground = new JsonMapParser().parse(Gdx.files.internal("map/glassStage.json").readString()).layers.head
  val wall = new JsonMapParser().parse(Gdx.files.internal("map/woodwall.json").readString()).layers.head
}
