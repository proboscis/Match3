package com.glyph.scala.game.puzzle.model.match_puzzle

import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 * @author glyph
 */
trait Panel{
  def matchTo(other:Panel):Boolean
}
trait OnMatch extends Panel{
  def onMatch(matched:Seq[Panel])
}
trait MaybeDestroyed extends Panel{
  def isDestroyed:Boolean
}
trait DestroyEffect extends Panel{
  def onDestroy(controller:PuzzleGameController)
}
