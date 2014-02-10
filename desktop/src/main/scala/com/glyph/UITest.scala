package com.glyph

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.glyph._scala.lib.libgdx.game.ScreenTester
import com.glyph._scala.test.SkinTest

/**
 * @author glyph
 */
object UITest {
  def main(args: Array[String]) {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    cfg.height = 1000
    cfg.width = 1000
    cfg.useGL20 = true
    new LwjglApplication(new SkinTest, cfg)
  }
}
