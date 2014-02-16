package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.g3d.decals.{GroupStrategy, DecalBatch}
import com.glyph._scala.lib.util.Disposable
import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
class DecalRenderer extends RequireStrategy with Disposable {
  protected val decals = new ListBuffer[DecalDrawable]()
  protected val batch = new DecalBatch()
  //TODO カメラセットのタイミングはインスタンス作成時っぽい

  def add(d: DecalDrawable) {
    decals += d
  }

  def remove(d: DecalDrawable) {
    decals -= d
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
