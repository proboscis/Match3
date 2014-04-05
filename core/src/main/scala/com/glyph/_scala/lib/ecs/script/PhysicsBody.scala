package com.glyph._scala.lib.ecs.script

import com.badlogic.gdx.physics.box2d.{Body, World, BodyDef}
import com.glyph._scala.lib.ecs.system.Physics
import com.glyph._scala.lib.ecs.Entity
import com.glyph.ClassMacro
import ClassMacro._
import com.badlogic.gdx.math.Vector2

/**
 * @author glyph
 */
class PhysicsBody(var bodyGenerator:()=>Body) extends Script{
  var body:Body = null
  var transform:Transform = null
  val tmp = new Vector2
  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    body = bodyGenerator()
    transform = self.getScript[Transform]
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    transform.matrix.setToTranslation(tmp.set(body.getPosition).scl(100f))
    log(transform.matrix)
  }

  override def reset(): Unit = {
    entity.scene.getSystem[Physics].world.destroyBody(body)
    super.reset()
    body = null
    bodyGenerator = null
    transform = null
  }
}
