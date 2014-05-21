package com.glyph._scala.lib.ecs.component

import com.glyph._scala.lib.ecs.{Entity, IsComponent, Component}
import com.glyph._scala.lib.ecs.script.Script
import ElapsedTime._
/**
 * @author glyph
 */
class ElapsedTime extends Component{
  var elapsedTime = 0f
  def reset(){
    elapsedTime = 0f
  }
}

object ElapsedTime{
  implicit object ElapsedTimeIsComponent extends IsComponent[ElapsedTime]
}
class ElapsedTimeUpdater extends Script{
  var et:ElapsedTime = null
  override def initialize(self: Entity): Unit = {
    super.initialize(self)
    et = self.component[ElapsedTime]
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    et.elapsedTime += delta
  }

  override def reset(): Unit = {
    super.reset()
    et = null
  }
}