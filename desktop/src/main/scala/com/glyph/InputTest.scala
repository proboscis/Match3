package com.glyph

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.glyph.scala.test.TestRunner
import com.badlogic.gdx.{InputAdapter, Gdx, Game}
import com.badlogic.gdx.scenes.scene2d.{Touchable, InputEvent, Stage, InputListener}
import com.badlogic.gdx.scenes.scene2d.ui.Table

/**
 * @author glyph
 */
object InputTest {
  def main(args: Array[String]) {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    cfg.height = 800
    cfg.width = 800
    cfg.useGL20 = true
    new LwjglApplication(new Game {
      def create(): Unit = {
        val stage = new Stage
        val table = new Table
        stage.addActor(table)
        Gdx.input.setInputProcessor(stage)
        table.setTouchable(Touchable.enabled)
        stage.setKeyboardFocus(table)
        table.addListener(new InputListener{
          override def keyDown(event: InputEvent, keycode: Int): Boolean = {
            println(keycode)
            super.keyDown(event, keycode)
            true
          }
        })
      }
    }, cfg)
  }
}
