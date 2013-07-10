package test

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.glyph.scala.DebugGame
import com.badlogic.gdx.Game

/**
 *
 * @author glyph
 */
trait TestGame extends Game{
  self =>
  def main(args: Array[String]) {
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    cfg.height = 700
    cfg.width = 320
    cfg.useGL20 = true
    new LwjglApplication(self, cfg)
  }
}
