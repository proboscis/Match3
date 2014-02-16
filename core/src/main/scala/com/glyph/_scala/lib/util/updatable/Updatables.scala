package com.glyph._scala.lib.util.updatable

import com.glyph._scala.lib.util.collection.list.DoubleLinkedList
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
 * @author glyph
 */
trait Updatables extends Updatable {
  val children = new ArrayBuffer[Updatable]

  def add(u: Updatable) {
    children += u
  }

  def remove(u: Updatable) {
    children += u
  }

  override def update(delta: Float) {
    super.update(delta)
    var i = 0
    val l = children.size
    while (i < l){
      children(i).update(delta)
      i += 1
    }
  }
}
