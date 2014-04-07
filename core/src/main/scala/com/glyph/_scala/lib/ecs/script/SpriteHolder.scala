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
 * @author glyph
 */
class SpriteHolder extends Script{
  val tmp = new Vector2
  val sprite:Sprite = new Sprite
  var transform:Transform = null
  var system:SpriteRenderer = null
  var tint:Tint = null

  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    transform = entity.component[Transform]
    system = entity.scene.getSystem[SpriteRenderer]
    tint = entity.component[Tint]
    system.sprites += sprite
  }


  override def update(delta: Float): Unit = {
    super.update(delta)
    transform.matrix.getTranslation(tmp)
    sprite.setX(tmp.x-sprite.getWidth/2)
    sprite.setY(tmp.y-sprite.getHeight/2)
    sprite.setColor(tint.color)
  }

  override def reset(): Unit = {
    super.reset()
    if(system != null) system.sprites -= sprite
    transform = null
    system = null
  }
}
