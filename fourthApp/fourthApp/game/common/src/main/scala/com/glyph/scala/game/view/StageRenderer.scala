package com.glyph.scala.game.view

import com.glyph.scala.lib.util.drawable.DecalDrawable
import com.badlogic.gdx.graphics.g3d.decals.{Decal, DecalBatch}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.util.modifier.{V3VaryTo, V3Delay, V3Animator}
import com.badlogic.gdx.math.{Interpolation, Vector3}
import com.glyph.scala.lib.util.update.Task

/**
 * @author glyph
 */
class StageRenderer extends DecalDrawable with Task {
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
  val mapDecals = map.foldLeft((Seq[Decal](), Seq[V3Animator]())) {
    (seq, t) => {
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
        val duration: Float = i * (1f / 32f)

        override def apply(input: Vector3, delta: Float) {
          super.apply(input, delta)
        }
      })
      animator.addAction(new V3VaryTo {
        val duration: Float = 0.6f
        interpolation = Interpolation.exp10Out
        val end: Vector3 = new Vector3(decal.getPosition).sub(0, 10, 0)
      })
      i += 1
      (seq._1 :+ decal, seq._2 :+ animator)
    }
  }

  def draw(batch: DecalBatch) {
    mapDecals._1.foreach {
      batch.add(_)
    }
  }

  def onUpdate(delta: Float) {
    mapDecals._2.foreach {
      _.act(delta)
    }
  }
}
