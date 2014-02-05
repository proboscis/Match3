package com.glyph._scala.lib.libgdx.conversion

import com.glyph._scala.lib.util.updatable.task.Accessor
import com.badlogic.gdx.scenes.scene2d.Actor


/**
 * @author glyph
 */
object InterpolatorGdx extends InterpolatorGdxOps

trait InterpolatorGdxOps {
  object ActorAccessor{
    object XY extends Accessor[Actor]{
      def size: Int = 2

      def get(tgt: Actor, values: Array[Float]): Unit = {
        values(0) = tgt.getX
        values(1) = tgt.getY
      }

      def set(tgt: Actor, values: Array[Float]): Unit = {
        tgt.setPosition(values(0),values(1))
      }
    }
  }
}
