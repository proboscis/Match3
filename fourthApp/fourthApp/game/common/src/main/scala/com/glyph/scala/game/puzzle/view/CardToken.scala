package com.glyph.scala.game.puzzle.view

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.graphics.g2d.{BitmapFontCache, BitmapFont, SpriteBatch, Sprite}
import com.glyph.scala.lib.libgdx.actor.{ObsTouchable, FuncTouchable, ExplosionFadeout, OldDrawSprite}
import com.badlogic.gdx.Gdx
import com.glyph.scala.game.puzzle.model.Card

/**
 * @author glyph
 */
class CardToken(val card: Card, w: Float, h: Float) extends Actor with OldDrawSprite with ExplosionFadeout with ObsTouchable {
  // val sprite = new Sprite(AM.instance().get[Texture]("data/card" + MathUtils.random(1, 10) + ".png"))
  //Actionポイントの実装
  val sprite = new Sprite(CardToken.texture)
  val glyph = CardToken.random()
  //TODO　ユグドラシルの
  setColor(Color.LIGHT_GRAY)
  setSize(w*0.9f, h*0.9f)
  setOrigin(w / 2, h / 2)

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    val b = glyph.getBounds
    glyph.setPosition(getX + (getWidth - b.width) / 2, getY - (getHeight - b.height) / 3f + getHeight)
    glyph.setColor(Color.WHITE)
    glyph.draw(batch, getColor.a * parentAlpha)
  }
}

object CardToken {
  val yggdrasil = new BitmapFont(Gdx.files.internal("font/yggdrasil.fnt"), false)
  val keys = "YGGDRASIL".toCharArray

  def random(): BitmapFontCache = {
    mapping(keys(MathUtils.random(keys.length - 1)))
  }

  val mapping = Map(keys map {
    k =>
      val cache = new BitmapFontCache(yggdrasil)
      cache.setText("" + k, 0f, 0f)
      (k -> cache)
  }: _*)
  val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  val texture = new Texture(image)
}
