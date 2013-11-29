package com.glyph.scala.lib.libgdx.conversion

import com.glyph.scala.lib.util.animator.{AnimatedFloat2, Animating}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.g2d.Sprite

/**
 * @author glyph
 */
object GdxConversion {
  /*
  object AnimatingVector2X extends Animating[Vector2]{
    def get: Vector2 = ???
  }*/
  implicit object Animated2Sprite extends AnimatedFloat2[Sprite]{
    def getX(tgt: Sprite): Float = tgt.getX

    def getY(tgt: Sprite): Float = tgt.getY

    def setX(tgt: Sprite)(x: Float): Unit = tgt.setX(x)

    def setY(tgt: Sprite)(y: Float): Unit = tgt.setY(y)
  }
}
