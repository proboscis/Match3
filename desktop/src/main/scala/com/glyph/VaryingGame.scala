package com.glyph

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.Game
import com.glyph.scala.test.VaryingGame
import com.glyph.scala.lib.util.reactive.RFile

/**
 * @author glyph
 */
object VaryingGame {
  def main(args: Array[String]) {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    cfg.height = 800
    cfg.width = 800
    cfg.useGL20 = true
  //  RFile.enableChecking(1000)
    new LwjglApplication(new VaryingGame,cfg)
  }
}
