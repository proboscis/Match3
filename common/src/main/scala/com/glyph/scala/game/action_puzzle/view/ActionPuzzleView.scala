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
  val size = puzzle.SIZE
  val puzzleView = new GMatch3View[ActionPuzzle.APanel](size,size,_=>null)
  puzzle.panelAdd = puzzleView.panelAdd
  puzzle.panelRemove = puzzleView.panelRemove
  puzzle.panelMove = puzzleView.panelMove
}
