package com.glyph.scala.lib.libgdx.poolable

import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.lib.util.pool.Pooling

/**
 * @author glyph
 */
object PoolingGdx {
  implicit object PoolingSprite extends Pooling[Sprite]{
    def newInstance: Sprite = new Sprite()
    def reset(tgt: Sprite){
      tgt.setColor(1f)
      tgt.setScale(1f)
      tgt.setTexture(null)
      tgt.setOrigin(0f,0f)
//      tgt.setRegion(0f,0f,1f,1f)
      tgt.setPosition(0f,0f)
      tgt.setSize(0f,0f)
      tgt.setRotation(0f)
    }
  }
}
