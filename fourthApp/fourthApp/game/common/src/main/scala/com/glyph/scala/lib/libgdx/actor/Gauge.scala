package com.glyph.scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.util.observer.reactive.{Varying, Ref, Reactor, Var}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.lib.libgdx.TextureUtil

/**
 * @author glyph
 */
class Gauge(alpha:Varying[Float]) extends WidgetGroup{
  val rWidth = Var(getWidth)
  val rHeight = Var(getHeight)
  addActor(new Actor with DrawSprite with Reactor {
    actor=>
    val sprite = new Sprite(TextureUtil.dummy)
    setColor(Color.RED)
    val zero = math.max(_:Float,0f)
    react(alpha~rWidth~rHeight) {
      case a~w~h =>
        import com.badlogic.gdx.scenes.scene2d.actions.Actions._
        import com.badlogic.gdx.math.Interpolation._
        actor.clearActions()
        actor.setHeight(h)
        actor.addAction(sizeTo(zero(a*w), zero(h), 1f, exp10Out))
    }
    override def draw(batch: SpriteBatch, parentAlpha: Float) {
      super.draw(batch, parentAlpha)
      drawSprite(batch,sprite,parentAlpha)
    }
  })

  override def setWidth(width: Float) {
    super.setWidth(width)
    rWidth() = width
  }

  override def setHeight(height: Float) {
    super.setHeight(height)
    rHeight() = height
  }
}
