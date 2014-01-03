package com.glyph.scala.game.action_puzzle.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.game.action_puzzle.{Token, MyTrail, ParticleRenderer, APView}
import com.badlogic.gdx.graphics.Texture

/**
 * @author glyph
 */
trait Trailed[A,B<:Actor]{
  self:APView[A,B]=>
  def texture:Texture
  val trailRenderer = new ParticleRenderer[MyTrail](texture)
  addActor(trailRenderer)
  override protected def onTokenRemove(token: Token[A, B]){
    trailRenderer.addParticles(token.getX+token.getWidth/2,token.getY+token.getHeight/2,token.getColor)
  }
}
