package com.glyph.scala.lib.libgdx.game

import com.glyph.scala.lib.libgdx.screen.ScreenBuilder

/**
 * @author glyph
 */
class ScreenBuilderTester(builder:ScreenBuilder) extends ScreenBuilderSupport {
  //TODO コマンドライン引数にクラスを指定出来るようにする
  override def create() {
    super.create()
    setBuilder(builder)
  }
}
