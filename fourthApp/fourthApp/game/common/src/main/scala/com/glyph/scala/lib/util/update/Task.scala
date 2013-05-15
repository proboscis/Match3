package com.glyph.scala.lib.util.update
import com.glyph.scala.lib.util.Tree

/**
 * @author glyph
 */
trait Task extends Updatable with Tree {
  def update(delta: Float) {
    onUpdate(delta)
    foreach(_.update(delta))
  }
  def onUpdate(delta:Float)
}
