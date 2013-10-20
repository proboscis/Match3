package com.glyph.scala.game.puzzle.model.match_puzzle

import com.glyph.scala.game.puzzle.controller.PuzzleGameController

/**
 * @author glyph
 */
trait OnMatch extends Match3.Panel{
  def onMatch(matched:Seq[Match3.Panel])
}
trait MaybeDestroyed extends Match3.Panel{
  def isDestroyed:Boolean
}
trait DestroyEffect extends Match3.Panel{
  def onDestroy(controller:PuzzleGameController)
}
