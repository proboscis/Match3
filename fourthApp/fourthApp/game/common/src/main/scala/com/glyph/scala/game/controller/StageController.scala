package com.glyph.scala.game.controller

import com.glyph.scala.game.model.StageData
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.util.callback.Callback
import com.glyph.scala.lib.util.updatable.task.{Parallel, CompleteHook, TimedTask, Sequence}
import com.glyph.scala.lib.libgdx.TileRegionGenerator
import com.glyph.scala.lib.libgdx.json.JsonParser
import com.glyph.scala.lib.util.json.ScalaJSON
import com.glyph.scala.lib.util.scene.{SceneComponent, SceneNode, UpdatableNode}
import com.glyph.scala.lib.libgdx.decal.DecalNode
import com.glyph.scala.lib.libgdx.graphics.util.decal.Decal
import com.glyph.scala.lib.libgdx.decal.DecalTask.MoveTo

/**
 * @author glyph
 */
class StageController(stage: StageData) extends UpdatableNode with DecalNode {
  val onStageReady = new Callback
  val tileGenerator = new TileRegionGenerator("data/TileA4.png", 8, 8)

  JsonParser("json/test.json") load{
    import com.glyph.scala.lib.util.json.JSON._
    json=>
    val dur:Float = json.duration
    clear()//remove all components
    //TODO reloadで再起動
    val parallel = new Parallel with CompleteHook with SceneNode
    parallel onComplete {
      onStageReady()
    }
    //TODO シーングラフのひとつということで管理する
    this += parallel
    val ground = stage.ground
    ground.data.foldLeft(0) {
      (i, t) => {
        val d = new Decal(tileGenerator.createRegion(t)) with SceneComponent
        d.setWidth(0.5f)
        d.setHeight(0.5f)
        d.rotateX(-90)
        d.setPosition(
          (i % ground.width - ground.height / 2) * d.getWidth + d.getWidth / 2,
          10,
          ((i / ground.height) - ground.height / 2) * d.getHeight + d.getHeight / 2)
        val animator = new Sequence {}
        animator.add(new TimedTask {
          val duration: Float = MathUtils.random(1f, 2f)
        })
        animator.add(new MoveTo {
          val decal: Decal = d
          val duration: Float = dur
          interpolation = Interpolation.exp10Out
          end.set(decal.getPosition).sub(0, 10f, 0)
        })
        parallel.add(animator)
        this += d
        i + 1
      }
    }
    val wall = stage.wall
    wall.data.foldLeft(0) {
      (i, m) => {
        val d = new Decal(tileGenerator.createRegion(m)) with SceneComponent
        d.setWidth(0.5f)
        d.setHeight(0.5f)
        d.setPosition(0, 0, -100)
        val sequence = new Sequence {}
        sequence.add(new TimedTask {
          val duration: Float = (wall.data.size - i) * 0.016f
        })
        sequence.add(new MoveTo {
          val decal: Decal = d
          val duration: Float = dur
          interpolation = Interpolation.elasticOut
          end.set((i % wall.width - wall.width / 2 + 0.5f) * d.getWidth, (wall.height - i / wall.width - 0.5f) * d.getHeight, (-ground.height / 2) * d.getHeight)
        })
        parallel.add(sequence)
        this += d
        i + 1
      }
    }
  }
}
