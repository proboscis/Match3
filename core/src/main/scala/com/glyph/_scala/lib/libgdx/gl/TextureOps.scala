package com.glyph._scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.glyph._scala.lib.libgdx.gl.TextureOps.TextureOpsImpl
import com.glyph._scala.lib.libgdx.{CanBeFileHandle, CanBe}

trait CanBeTexture[T] extends CanBe[T,Texture]
/**
 * @author glyph
 */
trait TextureOps {
  implicit def textureIsTextureRegion(tex:Texture):TextureRegion = new TextureRegion(tex)
  implicit def textureToTextureOpsImpl(tex:Texture):TextureOpsImpl = new TextureOpsImpl(tex)
  implicit def canBeTextureIsTexture[T:CanBeTexture](self:T) = implicitly[CanBeTexture[T]].apply(self)
  implicit def canBeFileHandleIsTexture[T:CanBeFileHandle]:CanBeTexture[T] = new CanBeTexture[T] {
    override def apply(self: T): Texture = new Texture(implicitly[CanBeFileHandle[T]].apply(self))
  }
}
object TextureOps extends TextureOps{
  import com.glyph._scala.lib.libgdx.drawable.DrawableOps._
  implicit class TextureOpsImpl(val tex:Texture) extends AnyVal{
    def drawable:TextureRegionDrawable = tex
    def region:TextureRegion = tex
  }
}