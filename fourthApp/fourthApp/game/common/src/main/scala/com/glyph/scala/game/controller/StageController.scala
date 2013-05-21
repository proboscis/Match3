package com.glyph.scala.game.controller

import com.glyph.scala.lib.util.drawable.{DecalList, DecalRenderer}
import com.glyph.scala.game.model.StageData
import com.glyph.scala.lib.util.updatable.Updatables
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.math.{Vector3, Interpolation, MathUtils}
import com.glyph.scala.lib.util.updatable.decal.DecalTask.MoveTo
import com.glyph.scala.lib.util.callback.Callback
import com.glyph.scala.lib.util.updatable.task.{Parallel, CompleteHook, TimedTask, Sequence}
import com.glyph.scala.lib.graphics.util.decal.Decal
import com.glyph.scala.lib.util.tile.TileRegionGenerator

/**
 * @author glyph
 */
class StageController(renderer: DecalRenderer, stage: StageData) extends Updatables {
  val tileGenerator = new TileRegionGenerator("data/TileA4.png",8,8)
  val onStageReady = new Callback
  val decalList = new DecalList {}
  val parallel = new Parallel with CompleteHook {
    def onComplete() {
      onStageReady()
    }
  }
  renderer.decals.push(decalList)
  this.add(parallel)
  val ground = stage.ground
  ground.data.foldLeft(0) {
    (i, t) => {
      val d = new Decal(tileGenerator.createRegion(t))
      d.setWidth(0.5f)
      d.setHeight(0.5f)
      d.rotateX(-90)
      d.setPosition(
        (i % ground.width - ground.height / 2) * d.getWidth+d.getWidth/2,
        10,
        ((i / ground.height) - ground.height / 2) * d.getHeight+d.getHeight/2)
      val animator = new Sequence {}
      animator.addTask(new TimedTask {
        val duration: Float = MathUtils.random(0f, 1f)
      })
      animator.addTask(new MoveTo {
        val decal: Decal = d
        val duration: Float = 1f
        interpolation = Interpolation.exp10Out
        end.set(decal.getPosition).sub(0, 10f, 0)
      })
      parallel.addTask(animator)
      decalList.add(d)
      i + 1
    }
  }
  val wall = stage.wall
  wall.data.foldLeft(0) {
        import MathUtils._
    (i, m) => {
      val d = new Decal(tileGenerator.createRegion(m))
      d.setWidth(0.5f)
      d.setHeight(0.5f)
      d.setPosition(0,0,-100)
      val sequence = new Sequence {}
      sequence.addTask(new TimedTask {
        val duration: Float = (wall.data.size-i)*0.016f
      })
      sequence.addTask(new MoveTo {
        val decal: Decal = d
        val duration: Float = 1f
        interpolation = Interpolation.elasticOut
        end.set((i%wall.width -wall.width/2+0.5f)*d.getWidth,(wall.height - i / wall.width-0.5f)*d.getHeight,(-ground.height/2)*d.getHeight)
      })
      parallel.addTask(sequence)
      decalList.add(d)
      i + 1
    }
  }//TODO scalaが3050行 (5/22) javaが480行
}
