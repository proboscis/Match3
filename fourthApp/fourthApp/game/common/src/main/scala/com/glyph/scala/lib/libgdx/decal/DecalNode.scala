package com.glyph.scala.lib.libgdx.decal

import com.glyph.scala.lib.util.scene.{SceneComponent, SceneNode}
import com.glyph.scala.lib.util.collection.LinkedList
import com.badlogic.gdx.graphics.g3d.decals.{Decal, DecalBatch}
import com.glyph.scala.lib.libgdx.drawable.DecalDrawable

/**
 * @author glyph
 */
trait DecalNode extends SceneNode with DecalDrawable {
  val drawables = new LinkedList[DecalDrawable]
  val decals = new LinkedList[Decal]


  override def +=(v: SceneComponent) {
    super.+=(v)
    if (v.isInstanceOf[DecalDrawable]) {
      drawables.push(v.asInstanceOf[DecalDrawable])
    } else if (v.isInstanceOf[Decal]) {
      decals.push(v.asInstanceOf[Decal])
    }
  }

  override def -=(v: SceneComponent) {
    super.-=(v)
    if (v.isInstanceOf[DecalDrawable]) {
      drawables.remove(v.asInstanceOf[DecalDrawable])
    } else if (v.isInstanceOf[Decal]) {
      decals.remove(v.asInstanceOf[Decal])
    }
  }

  def draw(batch: DecalBatch) {
    drawables.foreach(_.draw(batch))
    decals.foreach(batch.add(_))
  }

  override def clear() {
    super.clear()
    drawables.clear()
    decals.clear()
  }
}
