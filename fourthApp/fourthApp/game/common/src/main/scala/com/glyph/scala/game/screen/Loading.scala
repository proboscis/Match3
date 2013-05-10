package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.callback.Callback
import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{PerspectiveCamera, GL10, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{TextureRegion, SpriteBatch, Sprite}
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.graphics.g3d.decals.{DefaultGroupStrategy, CameraGroupStrategy, DecalBatch, Decal}
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
class Loading(game:ScalaGame) extends Screen{
  val onLoadingDone = new Callback
  val loadTex = new Texture(Gdx.files.internal("data/lightbulb32.png"))
  val decal = Decal.newDecal(new TextureRegion(loadTex),true)
  val batch = new DecalBatch()
  //val camera = new OrthographicCamera(Gdx.graphics.getWidth,Gdx.graphics.getHeight)
  val camera = new PerspectiveCamera(60f,Gdx.graphics.getWidth,Gdx.graphics.getHeight)
  batch.setGroupStrategy(new CameraGroupStrategy(camera))
  var timer = 0f
  override def render(delta: Float) {
    super.render(delta)

    timer += delta

    Gdx.gl.glClearColor(0,0,0,0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT)
    camera.position.x = MathUtils.sin(timer)*Gdx.graphics.getWidth/4
    camera.position.z = MathUtils.cos(timer)*Gdx.graphics.getWidth/4
    camera.near = 1
    camera.far = 1000
    camera.lookAt(0,0,0)
    camera.update()
    batch.add(decal)
    batch.flush()
  }
}
