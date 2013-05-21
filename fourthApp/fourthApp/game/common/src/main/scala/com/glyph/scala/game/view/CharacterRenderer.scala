package com.glyph.scala.game.view

import com.glyph.scala.lib.util.drawable.DecalDrawable
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.lib.graphics.util.decal.Decal

/**
 * @author glyph
 */
class CharacterRenderer extends DecalDrawable {
  val decal = new Decal(new TextureRegion(AM.instance.get("data/skeleton.png", classOf[Texture])), true)
  decal.setWidth(1f)
  decal.setHeight(1f)

  def draw(batch: DecalBatch) {
    batch.add(decal)
  }
}
