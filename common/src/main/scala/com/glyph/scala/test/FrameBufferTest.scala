package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{OrthographicCamera, GL20, Color, Texture}
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch, TextureRegion, Sprite}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.glyph.scala.lib.libgdx.actor.SpriteActor
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.Pixmap.Format

/**
 * @author glyph
 */
class FrameBufferTest extends ConfiguredScreen{
  val texture = new Texture(Gdx.files.internal("data/sword.png"))
  val dummy = new Texture(Gdx.files.internal("data/dummy.png"))
  val region = new TextureRegion(texture)
  val dummyRegion = new TextureRegion(dummy)
  val alpha = new Color(0,0,0,0.1f)
  val camera = new OrthographicCamera(Gdx.graphics.getWidth,Gdx.graphics.getHeight)
  val actor = new SpriteActor{
    override def draw(batch: Batch, parentAlpha: Float){
      batch.flush()
      frame.begin()
      super.draw(batch, parentAlpha)
      batch.flush()
      frame.end()
    }
  }
  val frame = new FrameBuffer(Format.RGB565,STAGE_WIDTH,STAGE_HEIGHT,false)
  val batch = new SpriteBatch()
  actor.sprite.setRegion(region)
  actor.setSize(100,100)
  backgroundColor.set(Color.BLACK)
  stage.addListener(new InputListener{
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      super.touchDown(event, x, y, pointer, button)
      true
    }
    override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int): Unit = {
      super.touchDragged(event, x, y, pointer)
      actor.setPosition(x,y)
    }
  })
  stage.addActor(actor)

  override def render(delta: Float){
    /*
    frame.begin()
    batch.enableBlending()
    batch.begin()
    batch.setColor(alpha)
    batch.draw(dummyRegion,0,0,STAGE_WIDTH,STAGE_HEIGHT)
    batch.end()
    frame.end()
   */
    super.render(delta)
    camera.update()
    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    val texture = frame.getColorBufferTexture
    batch.draw(texture,0,0,STAGE_WIDTH,STAGE_HEIGHT,0,0,texture.getWidth,texture.getHeight,false,true)
    batch.end()
  }
}
