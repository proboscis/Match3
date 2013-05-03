package com.glyph.scala.game.dungeon

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.GameConstants
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author glyph
 */
class DungeonRenderer(dungeon:DungeonManager) extends Actor{
  val sprite = new Sprite(AM.instance().get[Texture]("data/tile.png"))
  sprite.setSize(GameConstants.CELL_WIDTH,GameConstants.CELL_HEIGHT)
  lazy val dungeonMap = dungeon
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch,parentAlpha)
    var i = 0;
    while (i < dungeonMap.map.size) {
      sprite.setPosition(-i * sprite.getWidth, -sprite.getHeight)
      sprite.draw(batch, parentAlpha)
      i += 1
    }
  }
}
