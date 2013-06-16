package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Image, ScrollPane}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.scala.lib.util.tile.JsonMapParser
import com.glyph.scala.game.view.TileViewer

/**
 * @author glyph
 */
class ScrollTestScreen extends Screen{
  val stage = new Stage
  val root = new Table
  val viewer = new TileViewer("data/TileA4.png",8,8)
  val parser = new JsonMapParser
  parser.parse(Gdx.files.internal("map/test.json").readString())
  val pane = new ScrollPane(viewer)
  Gdx.input.setInputProcessor(stage)
  pane.setScrollingDisabled(false,false)
  root.setFillParent(true)
  root.add(pane).size(200,200).fill()
  stage.addActor(root)

  override def render(delta: Float) {
    super.render(delta)
    stage.act(delta)
    stage.draw()
    Table.drawDebug(stage)
  }
}
