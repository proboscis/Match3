package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.glyph._scala.lib.libgdx.graphics.util.decal.Decal
import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait DecalList extends DecalDrawable {
  val decals = new ListBuffer[Decal]()

  def add(decal: Decal) {
    decals += decal
  }

  def remove(decal: Decal) {
    decals -= decal
  }

  def draw(batch: DecalBatch) {
    decals foreach batch.add
  }
}
