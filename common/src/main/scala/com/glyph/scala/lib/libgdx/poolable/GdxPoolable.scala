package com.glyph.scala.lib.libgdx.poolable

import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.lib.util.pool.Pooling

/**
 * @author glyph
 */
object GdxPoolable {
  implicit object PoolableSprite extends Pooling[Sprite]{
    def newInstance: Sprite = new Sprite()
    def reset(tgt: Sprite){
      tgt.setColor(1f)
      tgt.setScale(1f)
      tgt.setTexture(null)
    }
  }
}
