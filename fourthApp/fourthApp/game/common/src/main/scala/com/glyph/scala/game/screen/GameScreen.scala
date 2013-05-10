package com.glyph.scala.game.screen

import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.scenes.scene2d.{Group, Actor, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.util.actor.{Debug, Touchable, Scissor}
import com.badlogic.gdx.scenes.scene2d.actions.{MoveToAction, Actions}
import com.badlogic.gdx.math.Interpolation

/**
 * @author glyph
 */
class GameScreen(game:ScalaGame) extends Screen{
  import com.glyph.scala.ScalaGame._
  val stage = new Stage(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,true)
  Gdx.input.setInputProcessor(stage)
  val root = new Table() with Scissor
  root.setSize(stage.getWidth,stage.getHeight)
  root.debug()

  val topGroup = new Group with Debug

  val action = Actions.action(classOf[MoveToAction])
  action.setPosition(300,300)
  action.setDuration(1)
  action.setInterpolation(Interpolation.exp10Out)

  root.add(topGroup)
  root.row()
  root.add()
  stage.addActor(root)

  def render(delta: Float) {
    stage.act(delta)
    stage.draw()
    Table.drawDebug(stage)
  }

  def resize(w: Int, h: Int) {

  }

  def show() {}

  def hide() {}

  def pause() {}

  def resume() {}

  def dispose() {}
}
