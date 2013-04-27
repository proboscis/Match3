package com.glyph.scala.game.interface.renderer

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.game.component.DungeonMap
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.GameConstants

/**
 * @author glyph
 */
class DungeonRenderer extends AbstractRenderer {
  val sprite = new Sprite(AM.instance().get[Texture]("data/tile.png"))
  sprite.setSize(GameConstants.CELL_WIDTH,GameConstants.CELL_HEIGHT)
  lazy val dungeonMap = renderer.owner.getMember[DungeonMap]
  def draw(batch: SpriteBatch, parentAlpha: Float) {
    var i = 0;
    while (i < dungeonMap.map.size) {
      sprite.setPosition(-i * sprite.getWidth, -sprite.getHeight)
      sprite.draw(batch, parentAlpha)
      i += 1
    }
  }
}
