package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.scenes.scene2d.ui.{ScrollPane, Table}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Stage}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL10
import com.glyph.scala.game.table.{TileViewer, GameTable}
import com.glyph.scala.ScalaGame

/**
 * @author glyph
 */
class DebugScreen extends Screen {
  val STAGE_WIDTH = ScalaGame.VIRTUAL_WIDTH * 2
  val STAGE_HEIGHT = ScalaGame.VIRTUAL_HEIGHT

  println("DebugScreen")
  val stage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true)
  Gdx.input.setInputProcessor(stage)
  val root = new Table
  root.addListener(new InputListener {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      println("touch")
      super.touchDown(event, x, y, pointer, button)
    }
  })
  root.debug()
  root.setSize(STAGE_WIDTH, STAGE_HEIGHT)
  stage.addActor(root)
  val ui = new Table()
  ui.debug()
  val gameRoot = new Table
  gameRoot.debug()
  val game = new GameTable
  gameRoot.add(game).size(ScalaGame.VIRTUAL_WIDTH, ScalaGame.VIRTUAL_HEIGHT)
  game.debug()
  root.add(gameRoot).size(STAGE_WIDTH / 2, STAGE_HEIGHT)
  root.add(ui).size(STAGE_WIDTH / 2, STAGE_HEIGHT)
  val viewer = new TileViewer("data/TileA4.png", 8, 8)
  val scroll = new ScrollPane(viewer)
  scroll.setScrollingDisabled(false, false)
  ui.add(scroll).size(300, 300)
  root.layout()

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    stage.act(delta)
    stage.draw()
    Table.drawDebug(stage)
  }

  override def resize(w: Int, h: Int) {
    super.resize(w, h)
    stage.setViewport(STAGE_WIDTH, STAGE_HEIGHT, true)
    stage.getCamera.translate(-stage.getGutterWidth, -stage.getGutterHeight, 0)
  }
}
