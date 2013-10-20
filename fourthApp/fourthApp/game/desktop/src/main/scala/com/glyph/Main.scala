package com.glyph

import com.badlogic.gdx.backends.lwjgl._
import com.glyph.scala.DebugGame
import com.badlogic.gdx.tools.imagepacker.TexturePacker2

object Main {
  def main(args: Array[String]) {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    val ratio = 9d / 16d
    val height = 1920 / 3
    cfg.height = height
    cfg.width = (height * ratio).toInt
    cfg.useGL20 = true

    //TexturePacker2.process("./","./skin","default")
    new LwjglApplication(new DebugGame(), cfg)
  }
}