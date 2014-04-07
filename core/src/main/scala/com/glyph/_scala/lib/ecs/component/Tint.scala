package com.glyph._scala.lib.ecs.component

import com.glyph._scala.lib.ecs.{IsComponent, Component}
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class Tint extends Component{
  val color = new Color
  def reset(){
    color.set(Color.WHITE)
  }
}
object Tint{
  implicit object TintIsComponent extends IsComponent[Tint]
}
