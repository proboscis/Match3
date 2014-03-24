package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import scala.language.implicitConversions
import com.glyph._scala.lib.libgdx.gl.CanBeTexture
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

/**
 * @author glyph
 */
trait GdxStringOps {
  trait Loadable[T]
  implicit object LoadableTexture extends Loadable[Texture]
  implicit object LoadableSkin extends Loadable[Skin]
  implicit object LoadableTextureAtlas extends Loadable[TextureAtlas]
  implicit def strToAsset[T](str:String)(implicit assets:AssetManager,ev:Loadable[T]):T =assets.get[T](str)
  implicit object stringCanBeInternalFileHandle extends CanBeFileHandle[String]{
    override def apply(self: String): FileHandle = Gdx.files.internal(self)
  }
}
object GdxStringOps extends GdxStringOps{
  implicit class fileToAsset(val file: String) extends AnyVal {
    def fromAssets[T](implicit am: AssetManager): T = am.get[T](file)
  }
}
