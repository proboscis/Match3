package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.graphics.GL10
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.util.actor.{Scissor, Renderer}

/**
 * @author glyph
 */
class DecalTableTest extends Screen{
  import ScalaGame._
  //TODO Actorの領域内で任意のレンダリングを行う実装
  val stage = new Stage(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,true)
  val root = new Table
  root.setSize(stage.getWidth,stage.getHeight)
  root.debug()
  val table = new Table with Renderer with Scissor
  table.debug()
  root.add(table).size(400,400)
  root.row()
  root.add(new Table with Renderer with Scissor).size(200,300)
  stage.addActor(root)
  Gdx.input.setInputProcessor(stage)


  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    Table.drawDebug(stage)
    stage.act(delta)
    stage.draw()
  }
}
