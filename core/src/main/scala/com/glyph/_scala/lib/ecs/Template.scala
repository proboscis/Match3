package com.glyph._scala.lib.ecs

import com.glyph._scala.game.Glyphs._
import com.glyph._scala.lib.ecs.component.{Tint, SimplePhysics, Velocities, Transform}
import com.glyph._scala.lib.ecs.script.TrailHolder
import com.glyph._scala.lib.util.pool.GlobalPool._
import Tint._
/**
 * @author glyph
 */
object Template {
  def trail(implicit s: Scene): Entity = {
    val e = s.createEntity()
    e += auto[SimplePhysics]
    e += auto[Transform]
    e += auto[TrailHolder]
    e += auto[Velocities]
    e += auto[Tint]
    e
  }
}
