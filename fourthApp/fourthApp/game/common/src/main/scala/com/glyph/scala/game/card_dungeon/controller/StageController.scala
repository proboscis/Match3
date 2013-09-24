package com.glyph.scala.game.card_dungeon.controller

import com.badlogic.gdx.math.{Vector3, Interpolation, MathUtils}
import com.glyph.scala.lib.util.callback.Callback
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.libgdx.TileRegionGenerator
import com.glyph.scala.lib.libgdx.json.JsonParser
import com.glyph.scala.lib.util.scene.{SceneComponent, UpdatableNode}
import com.glyph.scala.lib.libgdx.decal.DecalNode
import com.glyph.scala.lib.libgdx.graphics.util.decal.Decal
import com.glyph.scala.lib.libgdx.decal.DecalTask.Move
import com.glyph.scala.game.model.cardgame.StageData
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.util.reactive.RFile

/**
 * @author glyph
 */
class StageController(stage: StageData) extends UpdatableNode with DecalNode {
  val onStageReady = new Callback
  val tileGenerator = new TileRegionGenerator("data/TileA4.png", 8, 8)

  JsonParser("json/test.json") load {
    import com.glyph.scala.lib.util.json.JSON._
    json =>

      val dur: Float = json.duration
      clear() //remove all components
      //TODOO reloadで再起動
      val parallel = new Parallel with CompleteHook with SceneComponent
      parallel onComplete {
        onStageReady()
      }
      //TODOO シーングラフのひとつということで管理する
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
          parallel.add(Sequence(
            Delay(MathUtils.random(1f, 2f)),
            Move(d) to d.getPosition.cpy().sub(0, 10f, 0) for_ 3f using Interpolation.exp10Out
          ))
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
          val end = new Vector3((i % wall.width - wall.width / 2f + 0.5f) * d.getWidth, (wall.height - i / wall.width - 0.5f) * d.getHeight, (-ground.height / 2) * d.getHeight)
          parallel.add(Sequence(
            Delay((wall.data.size - i) * 0.016f),
            Move(d) to end for_ dur using Interpolation.elasticOut
          ))
          this += d
          i + 1
        }
      }
  }
}
