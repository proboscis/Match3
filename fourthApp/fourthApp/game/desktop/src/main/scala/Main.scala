package com.glyph

import com.badlogic.gdx.backends.lwjgl._
import libgdx.Engine
import scala.ScalaGame

object Main extends App {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    cfg.height = 480
    cfg.width = 320
    cfg.useGL20 = true
    new LwjglApplication(new ScalaGame(), cfg)
    //new LwjglApplication(new Engine(), cfg)
}
