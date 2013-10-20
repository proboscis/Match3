package com.glyph.scala.lib.util.updatable

import com.glyph.scala.lib.util.collection.list.DoubleLinkedList

/**
 * @author glyph
 */
trait Updatables extends Updatable{
  val children = new DoubleLinkedList[Updatable]
  def add(u:Updatable){
    children.push(u)
  }
  def remove(u:Updatable){
    children.remove(u)
  }

  override def update(delta: Float) {
    super.update(delta)
    children.foreach(_.update(delta))
  }
}
