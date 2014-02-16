package com.glyph._scala.lib.util.scene

import com.glyph._scala.lib.util.updatable.Updatable
import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait UpdatableNode extends SceneNode with Updatable{
  val updatables = new ListBuffer[Updatable]

  override def +=(v: SceneComponent) {
    super.+=(v)
    if (v.isInstanceOf[Updatable]){
      updatables += (v.asInstanceOf[Updatable])
    }
  }

  override def -=(v: SceneComponent) {
    super.-=(v)
    if (v.isInstanceOf[Updatable]){
      updatables.-=(v.asInstanceOf[Updatable])
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
