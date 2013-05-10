package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{GL10, Color, PerspectiveCamera}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.graphics.util.{World, Grid}

/**
 * @author glyph
 */
class ShapeTestScene extends Screen{
  val camera = new PerspectiveCamera(60f,Gdx.graphics.getWidth,Gdx.graphics.getHeight)
  camera.near = 1
  camera.far = 1000
  var time = 0f
  val world = new World(1)

  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)

    camera.position.x = MathUtils.sin(time/2) * 10
    camera.position.z = MathUtils.cos(time/2) * 10
    camera.position.y = 3
    camera.lookAt(0, 0, 0)
    camera.update()
    time += delta
    world.render(camera)
  }
}
