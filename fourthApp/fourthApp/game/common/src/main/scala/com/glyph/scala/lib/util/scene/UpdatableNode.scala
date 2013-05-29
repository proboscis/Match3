package com.glyph.scala.lib.util.scene

import com.glyph.scala.lib.util.updatable.Updatable
import com.glyph.scala.lib.util.collection.LinkedList

/**
 * @author glyph
 */
trait UpdatableNode extends SceneNode with Updatable{
  val updatables = new LinkedList[Updatable]

  override def +=(v: SceneComponent) {
    super.+=(v)
    if (v.isInstanceOf[Updatable]){
      updatables.push(v.asInstanceOf[Updatable])
    }
  }

  override def -=(v: SceneComponent) {
    super.-=(v)
    if (v.isInstanceOf[Updatable]){
      updatables.remove(v.asInstanceOf[Updatable])
    }
  }

  override def update(delta: Float) {
    super.update(delta)
    updatables.foreach(_.update(delta))
  }

  override def clear() {
    super.clear()
    updatables.clear()
  }
}
