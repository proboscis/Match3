package com.glyph.scala.lib.util.animator

import com.glyph.scala.lib.util.updatable.task.{AutoFree, TimedTask}
import java.util

/**
 * @author glyph
 */
class Explosion[T:AnimatedFloat2] extends TimedTask with AutoFree{
  var targets:IndexedSeq[T] = null
  var gravityX:Float = 0f
  var gravityY:Float = 0f
  var velocities:Array[Float] = Array.emptyFloatArray
  var targetSize = targets.size
  val impl = implicitly[AnimatedFloat2[T]]
  def init(tgts:IndexedSeq[T],gX:Float,gY:Float,thetaRandom:()=>Float,powRandom:()=>Float){
    this.targets = tgts
    gravityX = gX
    gravityY = gY
    targetSize = targets.size
    velocities = if (targetSize*2 > velocities.size) new Array(targetSize*2) else velocities
    util.Arrays.fill(velocities,0f)
    setupInitialVelocity(thetaRandom,powRandom)
  }
  def setupInitialVelocity(theta:()=>Float,power:()=>Float){
    var i = 0
    val size = targetSize
    while(i < size){
      val t = theta()
      val p = power()
      import com.badlogic.gdx.math.MathUtils._
      velocities(i) = sin(t) * p
      velocities(i*2) = cos(t) * p
      i += 1
    }
  }

  override def update(delta: Float){
    super.update(delta)
    var i = 0
    val size = targetSize
    while(i < size){
      val ax = delta * gravityX
      val ay = delta * gravityY
      velocities(i) =  ax
      velocities(i*2) = ay
      val tgt = targets(i)
      impl.setX(tgt)(impl.getX(tgt)+velocities(i)*delta)
      impl.setY(tgt)(impl.getY(tgt)+velocities(i*2)*delta)
      i += 1
    }
  }

  override def reset(){
    super.reset()
    targets = null
    gravityX = 0f
    gravityY = 0f
    targetSize = 0
    util.Arrays.fill(velocities,0f)
  }
}