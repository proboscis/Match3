package com.glyph.scala.lib.util.updatable

import com.glyph.scala.lib.util.collection.list.DoubleLinkedList
import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait Updatables extends Updatable {
  val children = new ListBuffer[Updatable]

  def add(u: Updatable) {
    children += u
  }

  def remove(u: Updatable) {
    children += u
  }

  override def update(delta: Float) {
    super.update(delta)
    children.foreach(_.update(delta))
  }
}
