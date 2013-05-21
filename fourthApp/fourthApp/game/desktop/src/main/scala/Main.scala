package com.glyph

import com.badlogic.gdx.backends.lwjgl._
import scala.{DebugGame, ScalaGame}

object Main extends App {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    cfg.height = 700
    cfg.width = 1200
    cfg.useGL20 = true
    new LwjglApplication(new DebugGame(), cfg)
}
