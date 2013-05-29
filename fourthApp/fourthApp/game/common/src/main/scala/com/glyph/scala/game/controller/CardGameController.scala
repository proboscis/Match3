package com.glyph.scala.game.controller

import com.glyph.scala.game.model.CardGameModel
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.graphics.util.World
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils._
import com.glyph.scala.lib.util.scene.UpdatableNode
import com.glyph.scala.lib.libgdx.decal.DecalNode
import com.glyph.scala.lib.libgdx.drawable.{RequireStrategy, StrategyRenderer, DecalRenderer}
import com.glyph.scala.lib.libgdx.actor.PerspectiveRenderer
import com.badlogic.gdx.graphics.g3d.decals.{CameraGroupStrategy, GroupStrategy}

/**
 * @author glyph
 */
class CardGameController(root: Table, model: CardGameModel) extends UpdatableNode with DecalNode {
  self =>
  /**
   * initialization
   */
  private val renderer = new DecalRenderer
  renderer.add(this)

  val topGroup = new Actor with PerspectiveRenderer {
    val drawable = new StrategyRenderer {
      val renderer: RequireStrategy = self.renderer
      val strategy: GroupStrategy = new CameraGroupStrategy(camera)
      val world = new World(0.5f)
      override def draw(camera: Camera) {
        super.draw(camera)
        //world.draw(camera)
      }
    }
    var time = 0f
    override def act(delta: Float) {
      super.act(delta)
      //time += delta
      camera.fieldOfView = 20f
      camera.viewportWidth = bound.getWidth
      camera.viewportHeight = bound.getHeight
      camera.position.x = sin(time / 2) * 20
      camera.position.z = cos(time / 2) * 20
      camera.position.y = 3
      camera.lookAt(0, 2, 0)
    }
  }
  root.add(topGroup).expand(1, 1).fill()
  root.row()
  root.add(new CardTable(model.player.deque)).expand(1, 1).fill
  for (i <- 0 until 10) {
    model.player.deque.drawCard()
  }

  enterStage()

  /**
   * declare methods
   */
  def enterStage() {
    val stageController = new StageController(model.stage)
    stageController.onStageReady {
      println("startBattle!")
      startBattle()
    }
    this += stageController
  }

  def startBattle() {
    val battleController = new BattleController(model.battle)
    this += battleController
  }
}
