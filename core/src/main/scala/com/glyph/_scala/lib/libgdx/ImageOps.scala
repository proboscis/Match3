package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.glyph._scala.lib.libgdx.drawable.CanBeDrawable

trait CanBe[-Self,+Target]{
  def apply(self:Self):Target
}
trait CanBeImage[Self] extends CanBe[Self,Image]
/**
 * @author glyph
 */
trait ImageOps {
  implicit def drawableIsImage(drawable:Drawable):Image = new Image(drawable)
  implicit def canBeImageIsImage[T:CanBeImage](img:T) = implicitly[CanBeImage[T]].apply(img)
  implicit object drawableCanBeImage extends CanBeImage[Drawable]{
    override def apply(self: Drawable): Image = new Image(self)
  }
  implicit def canBeDrawableCanBeImage[T:CanBeDrawable] = new CanBeImage[T]{
    override def apply(self: T): Image = implicitly[CanBe[T,Drawable]].apply(self)
  }
}
object ImageOps extends ImageOps{

}
