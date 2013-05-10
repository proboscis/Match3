package com.glyph.scala.lib.util.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.graphics.util.World
import com.badlogic.gdx.math.{Rectangle, MathUtils}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.glyph.scala.lib.util.gl.ViewportStack

/**
 * @author glyph
 */
trait Renderer extends Actor {
  import MathUtils._
  val camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  camera.near = 1
  camera.far = 1000
  var time = 0f
  val world = new World(1)

  val area = new Rectangle()
  val bound = new Rectangle()

  def getBounds = ActorUtil.getBounds(getStage.getCamera)(area)(bound) _

  override def act(delta: Float) {
    super.act(delta)
    time += delta

  }

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    batch.end()

    area.set(getX, getY, getWidth, getHeight)
    getBounds(batch.getTransformMatrix)
    ViewportStack.push(bound)
    camera.viewportWidth = bound.getWidth
    camera.viewportHeight = bound.getHeight
    camera.position.x = sin(time / 2) * 10
    camera.position.z = cos(time / 2) * 10
    camera.position.y = 3
    camera.lookAt(0, 0, 0)
    camera.update()
    world.render(camera)
    //super.draw(batch, parentAlpha)
    ViewportStack.pop()
    batch.begin()
  }
}
