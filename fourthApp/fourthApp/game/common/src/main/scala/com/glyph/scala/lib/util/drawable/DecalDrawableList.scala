package com.glyph.scala.lib.util.drawable

import com.glyph.scala.lib.util.collection.DoubleLinkedList
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch

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
