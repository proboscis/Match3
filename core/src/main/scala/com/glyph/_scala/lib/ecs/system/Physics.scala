package com.glyph._scala.lib.ecs.system

import com.glyph._scala.lib.ecs.Scene
import com.badlogic.gdx.physics.box2d.{Box2DDebugRenderer, World}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Camera

/**
 * @author glyph
 */
class Physics(camera:Camera) extends EntitySystem{
  val world = new World(new Vector2,true)
  val debugRenderer = new Box2DDebugRenderer()
  override def update(scene: Scene, delta: Float): Unit = {
    world.step(delta,2,2)
  }
  override def draw(scene: Scene): Unit = {
    debugRenderer.render(world,camera.combined)
  }
}
