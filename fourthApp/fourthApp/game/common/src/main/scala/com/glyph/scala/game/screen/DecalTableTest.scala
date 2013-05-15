package com.glyph.scala.game.screen

import com.glyph.scala.lib.util.screen.Screen
import com.badlogic.gdx.graphics.{Camera, Texture, GL10}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.util.actor.{Scissor, PerspectiveRenderer}
import com.glyph.scala.lib.graphics.util.World
import com.glyph.scala.lib.util.drawable.{DecalDrawable, DecalRenderer}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.decals.{DecalBatch, Decal}
import com.glyph.scala.lib.util.modifier._
import com.badlogic.gdx.math.{Interpolation, Vector3}

/**
 * @author glyph
 */
class DecalTableTest extends Screen {

  import ScalaGame._

  val drawable = new DecalDrawable {
    val mapTexture = TextureRegion.split(new Texture(Gdx.files.internal("data/TileA4.png")), 8, 8)
    val map = Seq(
      1, 1, 1, 1, 1, 1, 1, 1,
      1, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 1,
      1, 0, 0, 0, 0, 0, 0, 1,
      1, 1, 1, 1, 1, 1, 1, 1
    )
    val w = 8
    val h = 8
    var i = 0
    val mapDecals = map.map {
      t => {
        val region = t match {
          case 0 => mapTexture(2)(6)
          case 1 => mapTexture(0)(0)
        }
        val decal = Decal.newDecal(region)
        decal.setWidth(1)
        decal.setHeight(1)
        decal.rotateX(90)
        decal.setPosition((i % w - w / 2) * decal.getWidth, 10, (i / h - h / 2) * decal.getHeight)
        val animator = new V3Animator(decal.getPosition)
        animator.addAction(new V3Delay {
          val duration: Float = i%16*0.2f
          override def apply(input: Vector3, delta: Float) {
            super.apply(input, delta)
          }
        })
        animator.addAction(new V3VaryTo {
          val duration: Float = 0.5f
          interpolation = Interpolation.exp10Out
          val end: Vector3 = new Vector3(decal.getPosition).sub(0, 10, 0)
        })

        i += 1
        (decal, animator)
      }
    }

    def draw(batch: DecalBatch) {
      mapDecals.foreach {
        set => {
          set._2.act(0.016f)
          set._1.setScale(1)
          batch.add(set._1)
        }

      }
    }
  }

  val renderer = new DecalRenderer {
    val world = new World(1)

    override def draw(camera: Camera) {
      super.draw(camera)
      world.draw(camera)
    }
  }
  renderer.decals.push(drawable)

  //TODO Actorの領域内で任意のレンダリングを行う実装
  val stage = new Stage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true)
  val root = new Table
  root.setSize(stage.getWidth, stage.getHeight)
  root.debug()
  val table = new Actor with PerspectiveRenderer with Scissor {
    val drawable = renderer
  }
  root.add(table).size(stage.getWidth, stage.getHeight / 2)
  root.row()
  root.add(new Actor with PerspectiveRenderer with Scissor {
    val drawable = renderer
    time = 3.14f
    camera.fieldOfView = 45
  }).size(stage.getWidth, stage.getHeight / 2)
  stage.addActor(root)
  Gdx.input.setInputProcessor(stage)


  override def render(delta: Float) {
    super.render(delta)
    Gdx.gl.glClearColor(0, 0, 0, 0)
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT)
    Table.drawDebug(stage)
    stage.act(delta)
    stage.draw()
  }
}
