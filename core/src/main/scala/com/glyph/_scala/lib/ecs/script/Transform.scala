package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.math.Matrix3
import com.glyph._scala.lib.util.pool.Poolable

/**
 * @author glyph
 */
class Transform extends Poolable with Script{
  val matrix = new Matrix3()
  override def reset(): Unit = {
    super.reset()
    matrix.idt()
  }
}