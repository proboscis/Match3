package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch}
import com.glyph._scala.lib.util.gl.ViewportStack
import com.glyph._scala.lib.libgdx.drawable.RequireCamera

/**
 * @author glyph
 */
trait PerspectiveRenderer extends Actor {
  val drawable: RequireCamera
  val camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  camera.near = 1
  camera.far = 1000
  val area = new Rectangle()
  val bound = new Rectangle()
  //TODO strategyの切り替え実装
  override def draw(batch: Batch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    batch.end()

    area.set(getX, getY, getWidth, getHeight)
    ActorUtil.getBounds(getStage.getCamera)(area)(bound)(batch.getTransformMatrix)
    ViewportStack.push(bound)
    camera.update()
    drawable.draw(camera)
    ViewportStack.pop()

    batch.begin()
  }
}
