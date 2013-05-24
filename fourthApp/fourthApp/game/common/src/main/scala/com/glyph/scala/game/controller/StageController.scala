package com.glyph.scala.game.controller

import com.glyph.scala.lib.util.drawable.{DecalList, DecalRenderer}
import com.glyph.scala.game.model.StageData
import com.glyph.scala.lib.util.updatable.Updatables
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.util.updatable.decal.DecalTask.MoveTo
import com.glyph.scala.lib.util.callback.DeprecatedCallback
import com.glyph.scala.lib.util.updatable.task.{Parallel, CompleteHook, TimedTask, Sequence}
import com.glyph.scala.lib.graphics.util.decal.Decal
import com.glyph.scala.lib.libgdx.TileRegionGenerator
import com.glyph.scala.lib.libgdx.json.JsonParser
import com.glyph.scala.lib.util.json.ScalaJSON

/**
 * @author glyph
 */
class StageController(renderer: DecalRenderer, stage: StageData) extends Updatables {
  val tileGenerator = new TileRegionGenerator("data/TileA4.png", 8, 8)
  val onStageReady = new DeprecatedCallback
  val decalList = new DecalList {}
  //TODO reloadで再起動
  val parallel = new Parallel with CompleteHook {
    def onComplete() {
      onStageReady()
    }
  }
  renderer.add(decalList)
  this.add(parallel)
  val ground = stage.ground
  ground.data.foldLeft(0) {
    (i, t) => {
      val d = new Decal(tileGenerator.createRegion(t))
      d.setWidth(0.5f)
      d.setHeight(0.5f)
      d.rotateX(-90)
      d.setPosition(
        (i % ground.width - ground.height / 2) * d.getWidth + d.getWidth / 2,
        10,
        ((i / ground.height) - ground.height / 2) * d.getHeight + d.getHeight / 2)
      val animator = new Sequence {}
      animator.addTask(new TimedTask {
        val duration: Float = MathUtils.random(1f, 2f)
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
    (i, m) => {
      val d = new Decal(tileGenerator.createRegion(m))
      d.setWidth(0.5f)
      d.setHeight(0.5f)
      d.setPosition(0, 0, -100)
      val sequence = new Sequence {}
      sequence.addTask(new TimedTask {
        val duration: Float = (wall.data.size - i) * 0.016f
      })
      sequence.addTask(new MoveTo {
        val decal: Decal = d
        val duration: Float = 1f
        interpolation = Interpolation.elasticOut
        end.set((i % wall.width - wall.width / 2 + 0.5f) * d.getWidth, (wall.height - i / wall.width - 0.5f) * d.getHeight, (-ground.height / 2) * d.getHeight)
      })
      parallel.addTask(sequence)
      decalList.add(d)
      i + 1
    }
  }

  TestLoader.load{
    println(TestLoader.data)
  }

  object TestLoader extends JsonParser("json/test.json") {
    import com.glyph.scala.lib.util.json.JSON._
    var data = ""
    var data2 = ""
    protected def parse(json: ScalaJSON) {
      data = json.data
      data2 = json.data2
    }
    override def toString: String = data + "\n" + data2
  }
}
