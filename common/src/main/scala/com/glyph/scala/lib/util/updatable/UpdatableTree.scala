package com.glyph.scala.lib.util.updatable

import com.glyph.scala.lib.util.collection.DoubleTree

/**
 * @author glyph
 */
trait UpdatableTree extends Updatable with DoubleTree[UpdatableTree] {
  override def update(delta: Float) {
    super.update(delta)
    for (node <- this if node ne this){
      node.update(delta)
    }
  }

  protected def onUpdate(delta: Float) {}
}
