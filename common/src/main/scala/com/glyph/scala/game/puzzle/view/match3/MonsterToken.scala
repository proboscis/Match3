package com.glyph.scala.game.puzzle.view.match3

import com.badlogic.gdx.graphics.g2d.{Sprite, BitmapFontCache, SpriteBatch}
import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.view
import com.glyph.scala.lib.puzzle.Match3
import Match3.Panel
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.game.puzzle.model.monsters.Weapon
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class MonsterToken(assets:AssetManager,panel: Panel) extends PanelToken(assets,panel) with Reactor {

  import MonsterToken._

  //TODO Monsterパネルをタップで説明文表示
  //TODO Monsterクラスにdescriptionを追加しておく。
  val cache = fontMap.getOrElse(panel.getClass.getSimpleName.charAt(0) + "", fontMap("@"))(assets)

  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    val b = cache.getBounds
    cache.setPosition(getX + (getWidth - b.width) / 2, getY - (getHeight - b.height) / 3f + getHeight)
    cache.setColor(Color.WHITE)
    cache.draw(batch, getColor.a * parentAlpha)
  }
}

class WeaponToken(assets:AssetManager,panel: Weapon) extends PanelToken(assets,panel) with Reactor {
  val weaponSprite = new Sprite(assets.get("data/sword.png", classOf[Texture]))
  reactVar(ColorTheme.monster)(setColor)


  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch, weaponSprite, getColor.a * parentAlpha, color = Color.WHITE)
  }

  override def remove(): Boolean = {
    stopReact(ColorTheme.monster)
    super.remove()
  }
}

object MonsterToken {
  val keys = Array("D", "S", "@", "W")
  def random(assets:AssetManager): BitmapFontCache = {
    fontMap(keys(MathUtils.random(keys.length - 1)))(assets)
  }

  val fontMap = Map(keys map {
    c =>
      val func = (assets:AssetManager) => {
        val cache = new BitmapFontCache(view.commonFont(assets))
        cache.setText(c, 0, 0)
        cache
      }
      c -> func
  }: _*)
}
