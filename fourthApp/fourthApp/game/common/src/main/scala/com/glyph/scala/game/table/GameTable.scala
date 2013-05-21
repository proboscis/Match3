package com.glyph.scala.game.table

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.game.model.CardGameModel
import com.glyph.scala.game.controller.CardGameController
import com.glyph.scala.lib.util.updatable.Updatables

/**
 * @author glyph
 */
class GameTable extends Table with Updatables {
  val model = new CardGameModel
  val controller = new CardGameController(this, model)
  this.add(controller)

  override def act(delta: Float) {
    super.act(delta)
    update(delta)
  }

}
