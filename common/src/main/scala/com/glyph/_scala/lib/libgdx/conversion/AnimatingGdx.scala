package com.glyph._scala.lib.libgdx.conversion

import com.glyph._scala.lib.util.animator.{AnimatedFloat2, Animating}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch, Sprite}
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, SBDrawable}
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
object AnimatingGdx extends AnimatingGdxOps
trait AnimatingGdxOps {
  /*
  object AnimatingVector2X extends Animating[Vector2]{
    def get: Vector2 = ???
  }*/
  /*
  type Animated ={
    def getX():Float
    def getY():Float
    def setX(x:Float):Unit
    def setY(y:Float):Unit
  }*/
  //this uses reflection which does not work on android
  /*
  def generateAdapter[T<:Animated]:AnimatedFloat2[T] = new AnimatedFloat2[T] {
    def setY(tgt: T)(y: Float): Unit = tgt.setY(y)

    def getY(tgt: T): Float = tgt.getY()

    def setX(tgt: T)(x: Float): Unit = tgt.setX(x)

    def getX(tgt: T): Float =tgt.getX()
  }
  */
  def animatedActor[T<:Actor]:AnimatedFloat2[T] =  new AnimatedFloat2[T] {
    def setY(tgt: T)(y: Float): Unit = tgt.setY(y)

    def getY(tgt: T): Float = tgt.getY

    def setX(tgt: T)(x: Float): Unit = tgt.setX(x)

    def getX(tgt: T): Float =tgt.getX
  }
  def animatedSprite[T<:Sprite]:AnimatedFloat2[T] = new AnimatedFloat2[T] {
    def setY(tgt: T)(y: Float): Unit = tgt.setY(y)

    def getY(tgt: T): Float = tgt.getY

    def setX(tgt: T)(x: Float): Unit = tgt.setX(x)

    def getX(tgt: T): Float =tgt.getX
  }
  //TODO you need to keep these classes from proguard...
  //i want the implicit macro for these type classes.
  implicit val Animated2Sprite = animatedSprite[Sprite]
  implicit val AnimatedActor = animatedActor[Actor]
  implicit val AnimatedSpriteActor = animatedActor[SpriteActor]
}
