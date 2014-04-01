package com.glyph._scala.game.action_puzzle.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.game.action_puzzle.{Token, ScorePopper, APView}
import com.badlogic.gdx.graphics.g2d.BitmapFont

/**
 * @author glyph
 */
trait Scoring[A,B<:Actor]{
  self:APView[A,B]=>
  def font:BitmapFont
  val scorePopper = new ScorePopper(font)
  addActor(scorePopper)
  def popScore(token:Token[A,B],score:Int){
    scorePopper.showScoreParticle(token.getX + token.getWidth/2,token.getY,token.getHeight/2,score)
  }
}