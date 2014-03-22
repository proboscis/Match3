package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch

/**
 * @author glyph
 */
trait Tint extends DrawableCopy{
  val color = new Color()
  private val tmp = new Color
  override def draw(batch: Batch, a: Float, b: Float, c: Float, d: Float): Unit = {
    tmp.set(batch.getColor)
    batch.setColor(color)
    super.draw(batch, a, b, c, d)
    batch.setColor(tmp)
  }
}
