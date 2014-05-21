package com.glyph._scala.lib.ecs

import com.glyph._scala.game.Glyphs._
import com.glyph._scala.lib.ecs.component._
import com.glyph._scala.lib.ecs.script.{SpriteHolder, TrailHolder}
import com.glyph._scala.lib.util.pool.GlobalPool._
import Tint._
import ElapsedTime._
import com.badlogic.gdx.graphics.g2d.Sprite

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
  def particle(s:Scene):Entity ={
    val e = s.createEntity()
    e += auto[SimplePhysics]
    e += auto[Transform]
    e += auto[Velocities]
    e += auto[Tint]
    e += auto[ElapsedTime]
    e += auto[ElapsedTimeUpdater]
    e
  }
}
