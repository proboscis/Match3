package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.glyph._scala.game.action_puzzle.ComboPuzzle
import com.glyph._scala.game.builders.Builders
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, Scissor}
import com.badlogic.gdx.graphics.glutils.{ImmediateModeRenderer20, ImmediateModeRenderer10, FrameBuffer}
import com.badlogic.gdx.graphics.{GL10, OrthographicCamera, Pixmap}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{Rectangle, Matrix4}
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.util.Disposable

/**
 * @author glyph
 */
class BlurTest extends ConfiguredScreen{
  override def STAGE_WIDTH: Int = 1920
  import Builders._
  implicit val assetManager = new AssetManager
  val puzzle = new ComboPuzzle
  puzzle.time() = 1000000
  val table = new ActionPuzzleTable(puzzle)(
    roundRectTexture.forceCreate,
    particleTexture.forceCreate,
    dummyTexture.forceCreate,
    flat.forceCreate
  ) with FrameCapture
  root.add(table).width(960).fill.expandY
  val textureActor = new SpriteActor(table.buffer.getColorBufferTexture)
  root.add(textureActor).fill().expand
}

trait FrameCapture extends Actor with Disposable{
  val buffer = new FrameBuffer(Pixmap.Format.RGBA8888,1000,1000,false)
  val camera = new OrthographicCamera(960,1800)//this should be the same size as the actor's size.
  val scissorInfo = (camera,new Rectangle(0,0,1000,1000))//the view port must be the same size as the framebuffer's size.
  camera.setToOrtho(true,960,1800)//this must be updated whenever the size chanes.
  val projection = new Matrix4
  val transform = new Matrix4()
  val idt = new Matrix4()
  override def draw(batch: Batch, parentAlpha: Float): Unit ={
    batch.flush()
    projection.set(batch.getProjectionMatrix)
    transform.set(batch.getTransformMatrix)
    camera.update()
    batch.setTransformMatrix(idt)
    batch.setProjectionMatrix(camera.combined)
    Scissor.push(scissorInfo)
    buffer.begin()
    Gdx.gl.glClearColor(1,1,1,1)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
    super.draw(batch, parentAlpha)
    batch.flush()
    buffer.end()
    batch.setTransformMatrix(transform)
    batch.setProjectionMatrix(projection)
    Scissor.pop()
  }

  override def dispose(): Unit = {
    buffer.dispose()
  }
}
