package com.glyph.scala.lib.libgdx.drawable

import com.glyph.scala.lib.util.collection.LinkedList
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.glyph.scala.lib.libgdx.graphics.util.decal.Decal

/**
 * @author glyph
 */
trait DecalList extends DecalDrawable {
  val decals = new LinkedList[Decal]

  def add(decal: Decal) {
    decals.push(decal)
  }

  def remove(decal: Decal) {
    decals.remove(decal)
  }

  def draw(batch: DecalBatch) {
    decals.foreach(batch.add(_))
  }
}
