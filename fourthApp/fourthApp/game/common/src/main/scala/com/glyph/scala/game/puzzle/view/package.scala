package com.glyph.scala.game.puzzle

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle

/**
 * @author glyph
 */
package object view {
  println("view object")
  //if you don't call draw() of fonts somewhere, you'll end up with jaggy font somehow!!!
  val skin = new Skin(Gdx.files.internal("skin/default.json"))
  val yggdrasilFont = new BitmapFont(Gdx.files.internal("font/yggdrasil.fnt"), false)
  val commonFont = skin.getFont("default-font")
}
