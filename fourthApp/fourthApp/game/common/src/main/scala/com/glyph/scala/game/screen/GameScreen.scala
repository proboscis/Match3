package com.glyph.scala.game.screen

import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.util.actor.{PerspectiveRenderer, Scissor}
import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Camera, GL10}
import com.glyph.scala.lib.util.drawable.DecalRenderer
import com.glyph.scala.lib.graphics.util.World
import com.glyph.scala.game.model.CardGameModel
import com.glyph.scala.game.view.{BattleRenderer, StageRenderer}

/**
 * @author glyph
 */
class GameScreen(game: ScalaGame) extends Screen {

  import com.glyph.scala.ScalaGame._

  val model = new CardGameModel

  val stage = new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true)
  Gdx.input.setInputProcessor(stage)
  val renderer = new DecalRenderer {
    val world = new World(1)

    override def draw(camera: Camera) {
      super.draw(camera)
      world.draw(camera)
    }
  }
  renderer.decals.push(new StageRenderer)
  renderer.decals.push(new BattleRenderer)

  val root = new Table() with Scissor
  root.setSize(stage.getWidth, stage.getHeight)
  root.debug()
  val topGroup = new Actor with PerspectiveRenderer {
    val drawable = renderer
  }
  root.add(topGroup).expand().fill()
  root.row()
  root.add()
  stage.addActor(root)

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    stage.act(delta)
    stage.draw()
    Table.drawDebug(stage)
  }
}
