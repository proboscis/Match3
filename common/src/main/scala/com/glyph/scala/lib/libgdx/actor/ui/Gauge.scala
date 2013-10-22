package com.glyph.scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.lib.libgdx.TextureUtil
import com.glyph.scala.lib.util.reactive.{Varying, Var, Reactor}
import com.glyph.scala.lib.util.reactive
import com.glyph.scala.lib.libgdx.actor.{ReactiveSize, DrawSprite}
import com.glyph.scala.lib.util.lifting.Clamp
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
class Gauge(assets:AssetManager,alpha: Varying[Float],vertical:Boolean = false) extends WidgetGroup with ReactiveActor[Float] with ReactiveSize{
  import reactive._
  val visualAlpha = new Var(alpha(),"Gauge:visualAlpha")
  val filling = new Actor with DrawSprite with Reactor {
    actor =>
    val sprite = new Sprite(TextureUtil.dummy(assets))
    val zero = math.max(_: Float, 0f)
    reactVar(alpha ~ rWidth ~ rHeight) {
      case a ~ w ~ h =>
        //println("chg alpha:"+a)
        import com.badlogic.gdx.scenes.scene2d.actions.Actions._
        import com.badlogic.gdx.math.Interpolation._
        actor.clearActions()
        if(!vertical)actor.setHeight(h)
        if(vertical)actor.setWidth(w)
        val ww = if(vertical)w else w*a
        val hh = if(vertical)h*a else h
        actor.addAction(sizeTo(zero(ww), zero(hh), 1f, exp10Out))
    }
    override def setSize(width: Float, height: Float) {
      super.setSize(width, height)
      visualAlpha() =if(vertical) height/Gauge.this.getHeight else width / Gauge.this.getWidth
    }

    override def draw(batch: SpriteBatch, parentAlpha: Float) {
      super.draw(batch, parentAlpha)
      drawSprite(batch, sprite, parentAlpha)
    }
  }
  addActor(filling)
  override def setWidth(width: Float) {
    super.setWidth(width)
    rWidth() = width
  }

  override def setHeight(height: Float) {
    super.setHeight(height)
    rHeight() = height
  }

  def reactiveValue: Reactive[Float] = alpha

  override def setColor(color: Color) {
    super.setColor(color)
    filling.setColor(color)
  }

  override def setColor(r: Float, g: Float, b: Float, a: Float) {
    super.setColor(r, g, b, a)
    filling.setColor(r,g,b,a)
  }
}
