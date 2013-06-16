package com.glyph

import com.badlogic.gdx.backends.lwjgl._
import com.glyph.scala.DebugGame

object Main extends App {
  val cfg = new LwjglApplicationConfiguration()
  cfg.title = "Game"
  cfg.height = 700
  cfg.width = 600
  cfg.useGL20 = true
  new LwjglApplication(new DebugGame(), cfg)
}
