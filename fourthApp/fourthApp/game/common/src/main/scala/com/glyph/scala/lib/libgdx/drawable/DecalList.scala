package com.glyph.scala.lib.util.drawable

import com.glyph.scala.lib.util.collection.LinkedList
import com.glyph.scala.lib.graphics.util.decal.Decal
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch

/**
 * @author glyph
 */
trait DecalList extends DecalDrawable{
  val decals = new LinkedList[Decal]
  def add(decal:Decal){decals.push(decal)}
  def remove(decal:Decal){decals.remove(decal)}

  def draw(batch: DecalBatch) {
    decals.foreach(batch.add(_))
  }
}
