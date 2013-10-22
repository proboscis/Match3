package com.glyph.scala.lib.libgdx.game

import com.glyph.scala.lib.libgdx.screen.ScreenBuilder

/**
 * @author glyph
 */
class ScreenTester(target: ScreenBuilder) extends ScreenBuilderSupport {
  override def create() {
    super.create()
    setBuilder(target)
  }
}
