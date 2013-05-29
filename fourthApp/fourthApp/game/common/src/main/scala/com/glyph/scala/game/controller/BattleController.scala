package com.glyph.scala.game.controller

import com.glyph.scala.game.view.CharacterRenderer
import com.glyph.scala.lib.util.updatable.task.{Parallel, Sequence}
import com.badlogic.gdx.math.{Vector3, Interpolation}
import com.glyph.scala.game.model.Battle
import com.glyph.scala.lib.util.scene.{UpdatableNode, SceneComponent}
import com.glyph.scala.lib.libgdx.decal.DecalNode
import com.glyph.scala.lib.libgdx.decal.DecalTask.MoveTo
import com.glyph.scala.game.logic.{TurnProcessor, TurnManager}

/**
 * @author glyph
 */
class BattleController(battle: Battle) extends UpdatableNode with DecalNode {
  val PLAYER_POSITION = new Vector3(2, 6f, 0)
  val ENEMY_POSITION = new Vector3(-2, 6, 0)
  val turnManager = new TurnManager
  //TODO プレイヤーや敵の座標の決定
  //TODO 戦闘システムの実装
  val player = new CharacterRenderer
    with SceneComponent
    with TurnProcessor
    with UpdatableNode{
    action = new Action{
      override def onEnter() {
        super.onEnter()
        println("player action")
      }
    }
  }
  val enemy = new CharacterRenderer with SceneComponent with TurnProcessor{
  }

  enemy.decal.rotateY(180)
  val processor = new Parallel with SceneComponent
  player.decal.setPosition(PLAYER_POSITION.x, PLAYER_POSITION.y, PLAYER_POSITION.z)
  enemy.decal.setPosition(ENEMY_POSITION)
  processor.add(new MoveTo {
    val decal = player.decal
    val duration: Float = 1f
    interpolation = Interpolation.bounceOut
    end.set(decal.getPosition).sub(0, 5.5f, 0)
  })
  processor.add(new MoveTo {
    val decal = enemy.decal
    val duration: Float = 1f
    interpolation = Interpolation.bounceOut
    end.set(decal.getPosition).sub(0, 5.5f, 0)
  })
  this += processor
  this += player
  this += enemy

  turnManager.add(player)
  turnManager.add(enemy)
  turnManager.startCycle()
}
