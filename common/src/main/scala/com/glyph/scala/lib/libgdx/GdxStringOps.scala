package com.glyph.scala.lib.libgdx

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.graphics.g2d.TextureAtlas

/**
 * @author glyph
 */
trait GdxStringOps {
  implicit class fileToAsset(file: String) {
    def fromAssets[T](implicit am: AssetManager): T = am.get[T](file)
  }
  trait Loadable[T]
  implicit object LoadableTexture extends Loadable[Texture]
  implicit object LoadableSkin extends Loadable[Skin]
  implicit object LoadableTextureAtlas extends Loadable[TextureAtlas]
  implicit def strToAsset[T](str:String)(implicit assets:AssetManager,ev:Loadable[T]):T =assets.get[T](str)
}
