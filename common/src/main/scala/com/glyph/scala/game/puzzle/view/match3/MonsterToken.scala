package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.graphics.g2d.{Sprite, BitmapFontCache, SpriteBatch}
import com.badlogic.gdx.graphics.{Texture, Color}
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.view
import com.glyph.scala.game.puzzle.model.match_puzzle.Panel
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.game.puzzle.model.monsters.Weapon
import com.glyph.java.asset.AM

/**
 * @author glyph
 */
class MonsterToken(panel: Panel) extends PanelToken(panel) with Reactor{
  import MonsterToken._
  //TODO Monsterパネルをタップで説明文表示
  //TODO Monsterクラスにdescriptionを追加しておく。
  val cache = fontMap.getOrElse(panel.getClass.getSimpleName.charAt(0) + "",fontMap("@"))

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    val b = cache.getBounds
    cache.setPosition(getX + (getWidth - b.width) / 2, getY - (getHeight - b.height) / 3f + getHeight)
    cache.setColor(Color.WHITE)
    cache.draw(batch, getColor.a * parentAlpha)
  }
}
class WeaponToken(panel:Weapon)extends PanelToken(panel) with Reactor{
  val weaponSprite= new Sprite(AM.instance.get("data/sword.png",classOf[Texture]))
  reactVar(ColorTheme.monster)(setColor)


  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch,weaponSprite,getColor.a*parentAlpha,color = Color.WHITE)
  }

  override def remove(): Boolean = {
    stopReact(ColorTheme.monster)
    super.remove()
  }
}
object MonsterToken {
  val keys = Array("D","S","@","W")
  def random():BitmapFontCache={
    fontMap(keys(MathUtils.random(keys.length-1)))
  }
  val fontMap = Map(keys map {
    c =>
      val cache = new BitmapFontCache(view.commonFont)
      cache.setText(c,0,0)
      c -> cache
  }:_*)
}
