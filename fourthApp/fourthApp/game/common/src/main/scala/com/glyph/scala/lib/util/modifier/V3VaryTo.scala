package com.glyph.scala.lib.util.modifier

import com.badlogic.gdx.math.Vector3

/**
 * @author glyph
 */
trait V3VaryTo extends TemporalAction[Vector3]{
  val start = new Vector3()
  val end : Vector3

  override def onStart(input: Vector3) {
    super.onStart(input)
    start.set(input)
  }

  def update(input: Vector3, alpha: Float) {
    input.set(
      (end.x-start.x)*alpha + start.x,
      (end.y-start.y)*alpha + start.y,
      (end.z-start.z)*alpha + start.z
    )
  }
}
