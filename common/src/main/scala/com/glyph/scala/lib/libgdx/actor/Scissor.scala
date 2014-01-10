package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch}
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.Gdx

/**
 * @author glyph
 */

trait Scissor extends Actor {
  private val clipBounds = new Rectangle()
  private val scissors = new Rectangle()

  override def draw(batch:Batch, parentAlpha: Float) {
    batch.flush()
    clipBounds.set(getX, getY, getWidth, getHeight)
    ScissorStack.calculateScissors(getStage.getCamera,0,0,Gdx.graphics.getWidth,Gdx.graphics.getHeight, batch.getTransformMatrix, clipBounds, scissors)
    if (ScissorStack.pushScissors(scissors)) {
      super.draw(batch, parentAlpha)
      onScissor(batch,parentAlpha)
      ScissorStack.popScissors()
    } else {
      super.draw(batch, parentAlpha)
    }
    batch.flush()
  }
  def onScissor(batch:Batch,parentAlpha:Float){}
}
