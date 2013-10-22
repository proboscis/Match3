package com.glyph.scala.game.puzzle

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
package object view {
  println("view object")

  //if you don't call draw() of fonts somewhere, you'll end up with jaggy font somehow!!!
  val deprecatedSkin = new Skin(Gdx.files.internal("skin/default.json"))

  def skin(am: AssetManager) = am.get[Skin]("skin/default.json")

  def yggdrasilFont(am:AssetManager) = skin(am).getFont("yggdrasil-font")

  def commonFont(am:AssetManager) = skin(am).getFont("default-font")
}
