package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
trait GdxStringOps {

  implicit class fileToAsset(file: String) {
    def fromAssets[T](implicit am: AssetManager): T = am.get[T](file)
  }

}
