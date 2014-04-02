package com.glyph._scala.lib.libgdx.particle

/**
 * @author glyph
 */
class PRotation extends PModifier {
  override def onUpdate(entity: PEntity, delta: Float): Unit = {
    super.onUpdate(entity, delta)
    entity.transform.rotate(20*delta)
  }
}
