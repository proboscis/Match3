package com.glyph.scala.lib.libgdx.poolable

import com.badlogic.gdx.graphics.g2d.{TextureRegion, Sprite}
import com.glyph.scala.lib.util.pool.Pooling
import com.glyph.scala.lib.libgdx.actor.SpriteActor

/**
 * @author glyph
 */
object PoolingGdx extends PoolingGdxOps

trait PoolingGdxOps {

  implicit object PoolingSprite extends Pooling[Sprite] {
    def newInstance: Sprite = new Sprite()

    def reset(tgt: Sprite) {
      tgt.setColor(1f)
      tgt.setScale(1f)
      tgt.setTexture(null)
      tgt.setOrigin(0f, 0f)
      //      tgt.setRegion(0f,0f,1f,1f)
      tgt.setPosition(0f, 0f)
      tgt.setSize(0f, 0f)
      tgt.setRotation(0f)
    }
  }

  class PoolingSpriteActor extends Pooling[SpriteActor] {
    def newInstance: SpriteActor = new SpriteActor()
    def reset(tgt: SpriteActor) {
      tgt.clear()
    }
  }
  implicit object PoolingSpriteActorObject extends PoolingSpriteActor
  trait WithTextureRegion extends PoolingSpriteActor{
    def region:TextureRegion

    override def reset(tgt: SpriteActor): Unit = {
      super.reset(tgt)
      tgt.sprite.setRegion(region)
    }
  }
}
