package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.java.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.scala.lib.libgdx.actor.{Touchable, ExplosionFadeout, DrawSprite}

/**
 * @author glyph
 */
class CardToken(w:Float,h:Float) extends Actor with DrawSprite with ExplosionFadeout with Touchable{
  val sprite = new Sprite(AM.instance().get[Texture]("data/card" + MathUtils.random(1, 10) + ".png"))
  setSize(w,h)
  setOrigin(w/2,h/2)
}
