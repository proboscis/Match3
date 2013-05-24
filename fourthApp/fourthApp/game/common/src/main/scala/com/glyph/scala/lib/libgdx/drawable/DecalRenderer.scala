package com.glyph.scala.lib.util.drawable

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.decals.{ CameraGroupStrategy, DecalBatch}
import com.glyph.scala.lib.util.{Disposable}
import com.glyph.scala.lib.util.collection.LinkedList

/**
 * @author glyph
 */
class DecalRenderer extends RequireCamera with Disposable{
  protected val decals = new LinkedList[DecalDrawable]
  protected val batch = new DecalBatch()
  private val strategy = new CameraGroupStrategy(null)
  batch.setGroupStrategy(strategy)

  def add(d:DecalDrawable){
    decals.push(d)
  }
  def remove(d:DecalDrawable){
    decals.remove(d)
  }

  def draw(camera: Camera) {
    strategy.setCamera(camera)
    decals.foreach {_.draw(batch)}
    batch.flush()
  }

  def dispose() {
    batch.dispose()
    strategy.dispose()
  }
}
