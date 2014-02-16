package com.glyph._scala.lib.libgdx.game

import com.badlogic.gdx.{Gdx, Screen, Game}
import scala.util.{Failure, Success, Try}

/**
 * @author glyph
 */
class ScreenTester(screenName: String) extends Game {
  def create(): Unit = {
    Try {
      Class.forName(screenName).getConstructor().newInstance().asInstanceOf[Screen]
    } match {
      case Success(s) => setScreen(s)
      case Failure(e) => e.printStackTrace();Gdx.app.exit()
    }
  }
}
