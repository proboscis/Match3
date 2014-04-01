package com.glyph._scala.game.action_puzzle.screen

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.game.action_puzzle.{Token, MyTrail, ParticleRenderer, APView}
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.game.Glyphs

/**
 * @author glyph
 */
trait Trailed[A,B<:Actor]{
  self:APView[A,B]=>
  def trailRenderer:ParticleRenderer[MyTrail]
  import Glyphs._
  //val trailRenderer = new ParticleRenderer[MyTrail](texture)
  addActor(trailRenderer)
  override protected def onTokenRemove(token: Token[A, B]){
    self.onTokenRemove(token)
  }
  def explodeToken(token:Token[A,B],numParticle:Int){
    trailRenderer.addParticles(token.getX+token.getWidth/2,token.getY+token.getHeight/2,token.tgtActor.getColor,numParticle)
  }
}
