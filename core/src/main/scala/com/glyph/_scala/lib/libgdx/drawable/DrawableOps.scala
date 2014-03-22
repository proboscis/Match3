package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.{TextureRegionDrawable, Drawable}
import com.glyph._scala.lib.libgdx.gl.TextureOps
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * @author glyph
 */
trait DrawableOps {
  import TextureOps._
  implicit def textureIsDrawable(texture:Texture):TextureRegionDrawable = new TextureRegionDrawable(texture:TextureRegion)
}
object DrawableOps extends DrawableOps
