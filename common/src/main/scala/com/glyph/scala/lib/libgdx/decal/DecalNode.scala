package com.glyph.scala.lib.libgdx.decal

import com.glyph.scala.lib.util.scene.{SceneComponent, SceneNode}
import com.badlogic.gdx.graphics.g3d.decals.{Decal, DecalBatch}
import com.glyph.scala.lib.libgdx.drawable.DecalDrawable
import scala.collection.mutable.ListBuffer

/**
 * @author glyph
 */
trait DecalNode extends SceneNode with DecalDrawable {
  val drawables = new ListBuffer[DecalDrawable]
  val decals = new ListBuffer[Decal]


  override def +=(v: SceneComponent) {
    super.+=(v)
    v match {
      case drawable: DecalDrawable =>
        drawables += drawable
      case decal: Decal =>
        decals += decal
      case _ =>
    }
  }

  override def -=(v: SceneComponent) {
    super.-=(v)
    v match {
      case drawable: DecalDrawable =>
        drawables -= drawable
      case decal: Decal =>
        decals -= decal
      case _ =>
    }
  }

  def draw(batch: DecalBatch) {
    drawables.foreach(_.draw(batch))
    decals foreach batch.add
  }

  override def clear() {
    super.clear()
    drawables.clear()
    decals.clear()
  }
}
