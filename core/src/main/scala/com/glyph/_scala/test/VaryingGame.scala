package com.glyph._scala.test

import com.badlogic.gdx.Game
import com.glyph._scala.lib.util.reactive.{Reactor, VClass}
import com.glyph._scala.lib.libgdx.screen.ScreenBuilder
import com.glyph._scala.lib.libgdx.DrawFPS
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.glyph._scala.lib.libgdx.font.FontUtil

/**
 * @author glyph
 */
class VaryingGame extends Game with Reactor with Popped with DrawFPS {
  override def create() {
    super.create()
    val clazz = VClass[ScreenBuilder, VaryingScreen]
    reactSome(clazz) {
      c => setBuilder(c.newInstance())
    }
  }
  lazy val font = FontUtil.internalFont("font/corbert.ttf",25)
  def debugFont: BitmapFont = font
}
