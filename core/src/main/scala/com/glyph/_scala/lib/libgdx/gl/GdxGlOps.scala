package com.glyph._scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * @author glyph
 */
trait GdxGlOps {
  implicit def textureToTextureRegion(tex:Texture):TextureRegion = new TextureRegion(tex)
}
