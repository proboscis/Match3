package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.graphics.g2d.{Batch, TextureRegion, SpriteBatch, Sprite}
import com.badlogic.gdx.graphics.Texture

/**
 * @author glyph
 */
class SpriteActor extends DrawSprite{
  val sprite = new Sprite
  def this(texture:Texture)={
    this()
    setup(texture)
  }
  def reset() {
    sprite.setTexture(null)
    clear()
    remove()
  }
  override def draw(batch: Batch, parentAlpha: Float) {
    super.draw(batch, parentAlpha)
    drawSprite(batch, sprite, parentAlpha)
  }
  def setup(sprite:Sprite):this.type = {
    setSize(sprite.getWidth,sprite.getHeight)
    this.sprite.setTexture(sprite.getTexture)
    this.sprite.setRegion(sprite)
    this
  }
  def setup(texture:Texture):this.type = {
    setSize(texture.getWidth,texture.getHeight)
    this.sprite.setTexture(texture)
    this.sprite.asInstanceOf[TextureRegion].setRegion(0,0,texture.getWidth,texture.getHeight)
    this
  }
}

object SpriteActor {
  def apply(region: TextureRegion): SpriteActor = {
    val sp = new SpriteActor
    sp.sprite.setRegion(region)
    sp
  }
  def apply(texture:Texture):SpriteActor = new SpriteActor().setup(texture)
}
