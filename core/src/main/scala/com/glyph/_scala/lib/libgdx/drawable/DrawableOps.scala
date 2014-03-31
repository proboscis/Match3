package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.{TextureRegionDrawable, Drawable}
import com.glyph._scala.lib.libgdx.gl.{CanBeTexture, TextureOps}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.glyph._scala.lib.libgdx.CanBe

trait CanBeDrawable[Self] extends CanBe[Self,Drawable]
/**
 * @author glyph
 */
trait DrawableOps {
  import TextureOps._
  implicit def textureIsDrawable(texture:Texture):TextureRegionDrawable = new TextureRegionDrawable(texture:TextureRegion)
  implicit object textureCanBeDrawable extends CanBeDrawable[Texture]{
    override def apply(self: Texture): Drawable = self
  }
  implicit def canBeTextureCanBeDrawable[T:CanBeTexture] = new CanBeDrawable[T] {
    override def apply(self: T): Drawable = implicitly[CanBeTexture[T]].apply(self)
  }
  /*
  implicit def drawableIsCanBeDrawable[T<:Drawable](self:T) = new CanBeDrawable[T] {
    override def apply(self: T): Drawable = self
  }
  */
  implicit def canBeDrawableIsDrawable[T:CanBeDrawable](self:T) = implicitly[CanBeDrawable[T]].apply(self)
}
object DrawableOps extends DrawableOps
