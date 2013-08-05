package com.glyph.scala.game.puzzle.view.panel

import com.badlogic.gdx.graphics.g2d.{BitmapFontCache, SpriteBatch}
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.view
import com.glyph.scala.game.puzzle.model.puzzle.Panel

/**
 * @author glyph
 */
class MonsterToken(panel: Panel) extends PanelToken(panel) {
  import MonsterToken.random
  //TODO Monsterパネルをタップで説明文表示
  //TODO Monsterクラスにdescriptionを追加しておく。
  setColor(Color.DARK_GRAY)
  val cache = random()
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    val b = cache.getBounds
    cache.setPosition(getX + (getWidth - b.width) / 2, getY - (getHeight - b.height) / 3f + getHeight)
    cache.setColor(Color.WHITE)
    cache.draw(batch, getColor.a * parentAlpha)

  }
}

object MonsterToken {
  val keys = Array("D","S","@")
  def random():BitmapFontCache={
    fontMap(keys(MathUtils.random(keys.length-1)))
  }
  val fontMap = Map(keys map {
    c =>
      val cache = new BitmapFontCache(view.commonFont)
      cache.setText(c,0,0)
      (c ->cache)
  }:_*)
}
