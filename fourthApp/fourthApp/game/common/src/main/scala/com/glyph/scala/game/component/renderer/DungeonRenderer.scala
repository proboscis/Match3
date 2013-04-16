package com.glyph.scala.game.component.renderer

import com.glyph.scala.game.component.{DungeonGame, DungeonActor, Transform}
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.lib.entity_component_system.Entity
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.Glyph
import com.glyph.scala.game.GameConstants

/**
 * @author glyph
 */
class DungeonRenderer extends AbstractRenderer{
  var dungeon :DungeonGame = null
  val texture = AM.instance().get[Texture]("data/tile.png")
  val tileSprite = new Sprite(texture)
  tileSprite.setSize(GameConstants.CELL_WIDTH,GameConstants.CELL_HEIGHT)
  Glyph.log("dungeonRenderer","constructor")

  def draw(t: Transform, batch: SpriteBatch, alpha: Float) {
    var i = 0
    var x ,y = 0
    while ( i < dungeon.dungeonMap.size){
      x = - (i * GameConstants.CELL_WIDTH)
      y = 0
      tileSprite.setX(t.position.x + x)
      tileSprite.setY(t.position.y + y - GameConstants.CELL_HEIGHT)
      tileSprite.draw(batch,alpha)
      i += 1
    }
  }

  override def initialize(owner: Entity) {
    super.initialize(owner)
    Glyph.log("dungeonRenderer","initialize")
    dungeon = owner.directGet[DungeonGame]
  }
}
