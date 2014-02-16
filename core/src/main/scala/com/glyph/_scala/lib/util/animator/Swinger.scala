package com.glyph._scala.lib.util.animator

import com.glyph._scala.lib.util.updatable.task.{Task, AutoFree}
import com.glyph._scala.lib.util.Logging


/**
 * @author glyph
 */
object Swinger extends Logging{
  def update[T:AnimatedFloat2](radius:Float,originX:Float,originY:Float,tgt:T) ={
    val impl = implicitly[AnimatedFloat2[T]]
    ((et:Float)=>{
    //log("update:"+et)
    import com.badlogic.gdx.math.MathUtils._
    impl.setX(tgt)(originX + sin(et*PI2)*radius)
    impl.setY(tgt)(originY + cos(et*PI2)*radius)
  },()=>{
    impl.setX(tgt)(originX)
    impl.setY(tgt)(originY)
  })
  }
}
