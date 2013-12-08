package com.glyph.scala.lib.libgdx.conversion

import com.glyph.scala.lib.util.animator.{AnimatedFloat2, Animating}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.scala.lib.libgdx.actor.{SpriteActor, SBDrawable}
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
object AnimatingGdx {
  /*
  object AnimatingVector2X extends Animating[Vector2]{
    def get: Vector2 = ???
  }*/
  //TODO これ、ランタイムでリフレクションを使ってるぞ・・・・！？オソロシヤ
  type Animated ={
    def getX():Float
    def getY():Float
    def setX(x:Float):Unit
    def setY(y:Float):Unit
  }

  def generateAdapter[T<:Animated]:AnimatedFloat2[T] = new AnimatedFloat2[T] {
    def setY(tgt: T)(y: Float): Unit = tgt.setY(y)

    def getY(tgt: T): Float = tgt.getY()

    def setX(tgt: T)(x: Float): Unit = tgt.setX(x)

    def getX(tgt: T): Float =tgt.getX()
  }

  //TODO you need to keep these classes from proguard...
  implicit val Animated2Sprite = generateAdapter[Sprite]
  implicit val AnimatedActor = generateAdapter[Actor]
  implicit val AnimatedSpriteActor = generateAdapter[SpriteActor]
}
