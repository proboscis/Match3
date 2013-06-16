package com.glyph.scala.game.screen

import com.badlogic.gdx.scenes.scene2d.ui.{ScrollPane, Table}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.glyph.scala.ScalaGame
import com.glyph.scala.game.view.{TileViewer, GameTable}
import com.glyph.scala.lib.libgdx.screen.StagedScreen

/**
 * @author glyph
 */
class DebugScreen extends StagedScreen {
  val STAGE_WIDTH = ScalaGame.VIRTUAL_WIDTH * 2
  val STAGE_HEIGHT = ScalaGame.VIRTUAL_HEIGHT

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

  //root.layout()

  override def render(delta: Float) {
    super.render(delta)
    Table.drawDebug(stage)
  }
}
