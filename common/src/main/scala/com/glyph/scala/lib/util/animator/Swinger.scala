package com.glyph.scala.lib.util.animator

import com.glyph.scala.lib.util.updatable.task.{Task, AutoFree}
import com.glyph.scala.lib.util.Logging


/**
 * @author glyph
 */
object Swinger extends Logging{
  def update[T:AnimatedFloat2](radius:Float,originX:Float,originY:Float)(tgt:T)(alpha:Float){
    val impl = implicitly[AnimatedFloat2[T]]
    log("update")
    import com.badlogic.gdx.math.MathUtils._
    impl.setX(tgt)(originX + sin(alpha*PI2)*radius)
    impl.setY(tgt)(originY + cos(alpha*PI2)*radius)
  }
}
