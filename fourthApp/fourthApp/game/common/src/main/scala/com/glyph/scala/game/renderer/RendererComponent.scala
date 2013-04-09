package com.glyph.scala.game.renderer

import com.glyph.scala.lib.entity_component_system.{Entity, Component}
import com.glyph.scala.game.component.Transform
import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/07
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
class RendererComponent extends Component {
  var transform: Transform = null
  var renderer: Option[Renderer] = None

  override def initialize(owner: Entity) {
    transform = owner.get[Transform]
  }

  def setRenderer(r: Renderer) {
    renderer = Option.apply(r)
  }

  def render(batch:SpriteBatch,alpha:Float) {
    renderer map {
      _.render(batch,alpha,this)
    }
  }
}
