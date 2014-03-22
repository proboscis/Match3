package com.glyph._scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.glyph._scala.lib.libgdx.gl.TextureOps.TextureOpsImpl

/**
 * @author glyph
 */
trait TextureOps {
  implicit def textureIsTextureRegion(tex:Texture):TextureRegion = new TextureRegion(tex)
  implicit def textureToTextureOpsImpl(tex:Texture):TextureOpsImpl = new TextureOpsImpl(tex)
}
object TextureOps extends TextureOps{
  import com.glyph._scala.lib.libgdx.drawable.DrawableOps._
  implicit class TextureOpsImpl(val tex:Texture) extends AnyVal{
    def drawable:TextureRegionDrawable = tex
    def region:TextureRegion = tex
  }
}