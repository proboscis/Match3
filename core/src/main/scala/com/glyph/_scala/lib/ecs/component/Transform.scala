package com.glyph._scala.lib.ecs.component

import com.badlogic.gdx.math.Matrix3
import com.glyph._scala.lib.ecs.{Component, IsComponent}

/**
 * @author glyph
 */
class Transform extends Component{
  val matrix = new Matrix3()
  def reset(): Unit = {
    matrix.idt()
  }
}

object Transform{
  implicit object transformIsComponent extends IsComponent[Transform]
}