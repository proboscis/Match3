package com.glyph.scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.g3d.decals.DecalBatch
import com.glyph.scala.lib.util.collection.list.DoubleLinkedList

/**
 * @author glyph
 */
trait DecalDrawableList extends DecalDrawable {
  val drawables = new DoubleLinkedList[DecalDrawable]

  def draw(batch: DecalBatch) {
    drawables.foreach {
      _.draw(batch)
    }
  }

  def addDrawable(drawable: DecalDrawable) {
    drawables.push(drawable)
  }

  def removeDrawable(drawable: DecalDrawable) {
    drawables.remove(drawable)
  }
}
