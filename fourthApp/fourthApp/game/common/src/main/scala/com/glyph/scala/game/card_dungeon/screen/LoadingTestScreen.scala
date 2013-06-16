package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.callback.DeprecatedCallback
import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{PerspectiveCamera, GL10, Texture}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.graphics.g3d.decals.{CameraGroupStrategy, DecalBatch, Decal}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.graphics.util.World

/**
 * @author glyph
 */
class LoadingTestScreen(game: ScalaGame) extends Screen {
  val onLoadingDone = new DeprecatedCallback
  val world = new World(1)
  val batch = new DecalBatch()
  val camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth, Gdx.graphics.getHeight)
  camera.near = 1
  camera.far = 1000
  batch.setGroupStrategy(new CameraGroupStrategy(camera))
  var timer = 0f
  val mapTexture = TextureRegion.split(new Texture(Gdx.files.internal("data/TileA4.png")), 8, 8)
  val map = Seq(
    1,1,1,1,1,1,1,1,
    1,0,0,0,0,0,0,1,
    1,0,0,0,0,0,0,1,
    1,0,0,0,0,0,0,1,
    1,0,0,0,0,0,0,1,
    1,0,0,0,0,0,0,1,
    1,1,1,1,1,1,1,1
  )
  val w = 8
  val h = 8
  var i = 0
  val mapDecals = map.map{
    t => {
      val region = t match{
        case 0 => mapTexture(2)(6)
        case 1 => mapTexture(0)(0)
      }
      val decal = Decal.newDecal(region)
      decal.setWidth(1)
      decal.setHeight(1)
      decal.rotateX(90)
      decal.setPosition(i%w*decal.getWidth,0,i/h*decal.getHeight)
      i += 1
      decal
    }
  }
  override def render(delta: Float) {
    super.render(delta)

    timer += delta

    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    camera.position.x = MathUtils.sin(timer/2) * 10
    camera.position.z = MathUtils.cos(timer/2) * 10
    camera.position.y = 3

    camera.lookAt(0, 0, 0)
    camera.update()
    mapDecals.foreach {batch.add(_)}
    batch.flush()
    world.draw(camera)
  }
}
