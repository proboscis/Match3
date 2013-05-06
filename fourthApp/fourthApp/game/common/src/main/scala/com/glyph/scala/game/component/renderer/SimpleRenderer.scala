package com.glyph.scala.game.component.renderer

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, Sprite}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.component.{renderer}
import com.glyph.scala.game.component.value.Transform
import com.glyph.scala.game.GameConstants

/**
 * @author glyph
 */
trait SimpleRenderer extends Renderer{
  lazy val sprite = new Sprite(AM.instance().get[Texture]("data/skeleton.png"))
  lazy val transform = this.owner.get[Transform]
  override def draw(batch: SpriteBatch, parentAlpha: Float) {
    super.draw(batch,parentAlpha)
    sprite.setSize(GameConstants.CELL_WIDTH,GameConstants.CELL_HEIGHT)
    sprite.setPosition(transform.position.x,transform.position.y)
    sprite.setRotation(transform.direction.angle())
    sprite.draw(batch,parentAlpha)
  }
}
