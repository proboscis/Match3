package com.glyph.scala.lib.util.animator

import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.Logging


object Explosion extends Logging{
  def init(thetaRandom: () => Float, powRandom: () => Float, targetVelocities: ArrayBuffer[Float], numTarget: Int) {
    var i = 0
    val size = numTarget * 2
    while (i < size) {
      val t = thetaRandom()
      val p = powRandom()
      import com.badlogic.gdx.math.MathUtils._
      targetVelocities += sin(t) * p
      targetVelocities += cos(t) * p
      i += 1
    }
  }
  def update[T: AnimatedFloat2](gX: Float, gY: Float,viscosity:Float)(targets: IndexedSeq[T], velocities: collection.mutable.IndexedSeq[Float])(delta: Float) {
    var i,ix,iy = 0
    val size = targets.length
    val impl = implicitly[AnimatedFloat2[T]]
    while (i < size) {
      ix = i << 1
      iy = ix + 1
      val vx = velocities(ix)
      val vy = velocities(iy)
      var vvx =  vx * vx * viscosity
      if(vx > 0) vvx *= -1
      var vvy =  vy * vy * viscosity
      if(vy > 0)vvy *= -1
      val ax = delta * (gX + vvx)
      val ay = delta * (gY + vvy)
      velocities(ix) += ax
      velocities(iy) += ay
      val tgt = targets(i)
      impl.setX(tgt)(impl.getX(tgt) + velocities(ix) * delta)
      impl.setY(tgt)(impl.getY(tgt) + velocities(iy) * delta)
      i += 1
    }
  }
}