package com.glyph._scala.lib.libgdx.particle

import com.badlogic.gdx.math.Vector2

/**
 * @author glyph
 */
class PGravity extends PModifier{
  val tmp = new Vector2()
  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    var i = 0
    val children = entity.children
    val size = children.size
    while (i < size){
      val child = children(i)
      child.acc.add(child.transform.getTranslation(tmp).scl(-100*delta))
      i += 1
    }
    super.onUpdate(entity, delta)
  }
}
