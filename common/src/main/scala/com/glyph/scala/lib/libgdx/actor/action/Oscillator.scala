package com.glyph.scala.lib.libgdx.actor.action

import com.badlogic.gdx.scenes.scene2d.{Actor, Action}
import com.badlogic.gdx.math.{MathUtils, Interpolation}
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction

/**
 * @author glyph
 */
class Oscillator(x:Float,y:Float,frequency:Float,interpolation:Interpolation = Interpolation.linear) extends TemporalAction{
  var srcX = 0f
  var srcY = 0f
  override def setActor(actor: Actor) {
    super.setActor(actor)
    if (actor != null){
      srcX = actor.getX
      srcY = actor.getY
    }else{
      srcX = 0
      srcY = 0
    }
  }

  def update(alpha: Float) {
    import MathUtils._
    val radian = alpha * getDuration * frequency * PI2
    actor.setX(srcX + x*sin(radian))
    actor.setY(srcY + y*sin(radian))
    //println(alpha,srcY,actor.getY)
  }
}
