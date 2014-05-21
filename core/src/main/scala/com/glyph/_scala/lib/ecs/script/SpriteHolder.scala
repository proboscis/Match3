package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph._scala.lib.ecs.component.{Tint, Transform}
import com.glyph._scala.lib.ecs.Entity
import com.glyph._scala.lib.ecs.system.SpriteRenderer
import com.badlogic.gdx.math.{Vector2, Matrix3}
import Transform._
import Tint._
import com.glyph.ClassMacro._
/**
 * you must provide a register and an unregister each time.
 * @author glyph
 */
class SpriteHolder extends Script{
  val tmp = new Vector2
  val sprite:Sprite = new Sprite
  var transform:Transform = null
  var tint:Tint = null
  var register :Sprite=>Unit = null
  var unregister:Sprite=>Unit = null

  def setRegisters(reg:Sprite=>Unit,unreg:Sprite=>Unit):this.type ={
    register = reg
    unregister = unreg
    this
  }

  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    transform = entity.component[Transform]
    tint = entity.component[Tint]
    assert(register != null && unregister != null)
    register(sprite)
    sprite.setOrigin(sprite.getWidth/2,sprite.getHeight/2)
  }


  override def update(delta: Float): Unit = {
    super.update(delta)
    transform.matrix.getTranslation(tmp)
    sprite.setX(tmp.x-sprite.getWidth/2)
    sprite.setY(tmp.y-sprite.getHeight/2)
    sprite.setRotation(transform.matrix.getRotation)
    transform.matrix.getScale(tmp)
    sprite.setScale(tmp.x,tmp.y)
    sprite.setColor(tint.color)
  }

  override def reset(): Unit = {
    super.reset()
    transform = null
    if(unregister != null) unregister(sprite)
    register = null
    unregister = null
  }
}
