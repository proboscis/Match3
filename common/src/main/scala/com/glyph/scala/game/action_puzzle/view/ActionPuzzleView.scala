package com.glyph.scala.game.action_puzzle.view
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.{GMatch3, ActionPuzzle}
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Label, Table, WidgetGroup}
import com.glyph.scala.lib.libgdx.actor.Layered
import com.glyph.scala.game.action_puzzle.ActionPuzzle.APanel
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class ActionPuzzleView(assets:AssetManager,puzzle:ActionPuzzle,width:Int,height:Int) extends Table{
  debug()
  setSize(width,height)
  val root = new WidgetGroup with Layered
  val table = new Table
  val size = puzzle.SIZE
  val skin = assets.get[Skin]("skin/default.json")
  val puzzleView = new GMatch3View[ActionPuzzle.APanel](size,size,p=> new Label(p.n+"",skin) with GMatch3View.PanelToken[ActionPuzzle.APanel]{
    setColor(Color.WHITE)
    def panel: APanel = p
  })
  root.addActor(puzzleView)
  add(root).fill.expand
  puzzle.panelAdd = puzzleView.panelAdd
  puzzle.panelRemove = puzzleView.panelRemove
  puzzle.panelMove = puzzleView.panelMove
}
