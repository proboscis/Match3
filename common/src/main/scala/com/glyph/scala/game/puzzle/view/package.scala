package com.glyph.scala.game.puzzle

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.java.asset.AM
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */
package object view {
  println("view object")

  //if you don't call draw() of fonts somewhere, you'll end up with jaggy font somehow!!!
  val deprecatedSkin = new Skin(Gdx.files.internal("skin/default.json"))
  def skin = AM.instance().get[Skin]("skin/default.json")

  def yggdrasilFont = skin.getFont("yggdrasil-font")

  def commonFont = skin.getFont("default-font")
}
