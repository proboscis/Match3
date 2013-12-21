package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Batch, SpriteBatch}

/**
 * @author glyph
 */
trait AlphaMod extends Actor{
  private var _alpha = 1f
  override def draw(batch: Batch, parentAlpha: Float) {
    super.draw(batch, parentAlpha*_alpha)
  }
  def alpha = _alpha
  def alpha_=(a:Float){ _alpha = a}
}
