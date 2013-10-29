package com.glyph.scala.game.action_puzzle.view
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.ActionPuzzle
import com.badlogic.gdx.scenes.scene2d.ui.{Table, WidgetGroup}
import com.glyph.scala.lib.libgdx.actor.Layered

/**
 * @author glyph
 */
class ActionPuzzleView(assets:AssetManager,puzzle:ActionPuzzle) extends WidgetGroup{
  val root = new WidgetGroup with Layered
  val table = new Table
  val puzzleView = new GMatch3View[ActionPuzzle#APanel](6,6,_=>null)
}
