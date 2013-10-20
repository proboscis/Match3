package com.glyph.scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.g3d.decals.{GroupStrategy, DecalBatch}
import com.glyph.scala.lib.util.Disposable
import com.glyph.scala.lib.util.collection.LinkedList

/**
 * @author glyph
 */
class DecalRenderer extends RequireStrategy with Disposable {
  protected val decals = new LinkedList[DecalDrawable]
  protected val batch = new DecalBatch()
  //TODO カメラセットのタイミングはインスタンス作成時っぽい

  def add(d: DecalDrawable) {
    decals.push(d)
  }

  def remove(d: DecalDrawable) {
    decals.remove(d)
  }

  def draw(strategy: GroupStrategy) {
    batch.setGroupStrategy(strategy)
    decals.foreach {
      _.draw(batch)
    }
    batch.flush()
  }

  def dispose() {
    batch.dispose()
  }
}
