package com.glyph.scala.lib.libgdx.tween

import com.glyph.scala.lib.util.updatable.task.tween.Accessor
import com.glyph.scala.lib.util.Logging
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.Color
import aurelienribon.tweenengine.{Tween, TweenManager, TweenAccessor}

/**
 * @author proboscis
 */
object Test extends GdxTweenOps

trait GdxTweenOps extends Logging {
  Tween.setCombinedAttributesLimit(4)
  object SpriteAccessor extends TweenAccessor[Sprite]{
    Tween.registerAccessor(classOf[Sprite],this)
    val XY = 0
    val RGBA = 1
    def getValues(target: Sprite, tweenType: Int, returnValues: Array[Float]): Int = {
      tweenType match{
        case XY =>{
          returnValues(0) = target.getX
          returnValues(1) = target.getY
          2
        }
        case RGBA => {
          val c = target.getColor
          returnValues(0) = c.r
          returnValues(1) = c.g
          returnValues(2) = c.b
          returnValues(3) = c.a
          4
        }
      }
    }

    def setValues(target: Sprite, tweenType: Int, newValues: Array[Float]){
      tweenType match{
        case XY => {
          target.setX(newValues(0))
          target.setY(newValues(1))
        }
        case RGBA => {
          target.setColor(newValues(0),newValues(1),newValues(2),newValues(3))
        }
      }
    }
  }
}

