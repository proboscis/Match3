package com.glyph.scala.game.screen

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.{Game, Gdx}
import com.badlogic.gdx.graphics.GL10
import com.glyph.scala.game.controller.CardGameController
import com.glyph.scala.lib.util.scene.UpdatableNode
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.glyph.scala.game.model.cardgame.CardGameModel

/**
 * @author glyph
 */
class GameScreen(game: Game) extends Screen with UpdatableNode {

  import com.glyph.scala.ScalaGame._

  val model = new CardGameModel
  val root = new Table() with Scissor
  val controller = new CardGameController(root, model)
  this += (controller)
  val stage = new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true)
  root.setSize(stage.getWidth, stage.getHeight)
  root.debug()
  Gdx.input.setInputProcessor(stage)
  stage.addActor(root)

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    stage.act(delta)
    update(0.016f)
    stage.draw()
    Table.drawDebug(stage)
  }

}
