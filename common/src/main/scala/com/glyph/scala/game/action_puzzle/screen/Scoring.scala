package com.glyph.scala.game.action_puzzle.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.game.action_puzzle.{Token, ScorePopper, APView}

/**
 * @author glyph
 */
trait Scoring[A,B<:Actor]{
  self:APView[A,B]=>
  val scorePopper = new ScorePopper()
  addActor(scorePopper)
  def score:Int
  override protected def onTokenRemove(token: Token[A, B]): Unit = {
    self.onTokenRemove(token)
    scorePopper.showScoreParticle(token.getX + token.getWidth/2,token.getY,token.getHeight/2,score)
  }
}