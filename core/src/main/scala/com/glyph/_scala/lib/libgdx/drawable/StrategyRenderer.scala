package com.glyph._scala.lib.libgdx.drawable

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.decals.{GroupStrategy, CameraGroupStrategy}

/**
 * @author glyph
 */
trait StrategyRenderer extends RequireCamera{
  val strategy :GroupStrategy
  val renderer :RequireStrategy
  def draw(camera: Camera) {
    renderer.draw(strategy)
  }
}
