package com.glyph.scala.test

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import Actions._
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.libgdx.actor._
import com.glyph.scala.lib.util.reactive.Var
import com.glyph.scala.lib.util.updatable.reactive.Eased
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, WidgetGroup}
import com.badlogic.gdx.graphics.{Texture, Color}
import com.glyph.scala.lib.libgdx.actor.SpriteActor
import com.glyph.scala.lib.util.reactive.Reactor
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/**
 * @author glyph
 */
class EffectTest extends ConfiguredScreen {
  backgroundColor = Color.BLACK
  val skin = new Skin(Gdx.files.internal("skin/default.json"))
  val texture = new Texture(Gdx.files.internal("data/round_rect.png"))
  val view = new WidgetGroup with AdditiveBlend with SpriteBatchRenderer with Tasking with Updating
  val token = new SpriteActor(new Sprite(texture)) with Reactor
  token.setSize(100, 100)
  token.setPosition(0, 0)
  token.setColor(Color.valueOf("7cefff"))
  view.addActor(token)
  view.setSize(STAGE_WIDTH, STAGE_HEIGHT)
  root.add(view).fill.expand
  root.debug()
  root.invalidate()
  root.layout()
  val x = new Var(0f)
  val y = new Var(0f)
  def eased = Eased(_: Var[Float],Interpolation.exp10Out.apply, f => f / 100f)
  val easedX = eased(x)
  val easedY = eased(y)
  token.reactVar(easedX)(token.setWidth)
  token.reactVar(easedY)(token.setHeight)
  view.add(easedX)
  view.add(easedY)
  new Thread(new Runnable {
    def run() {
      1 :: 3 :: (-4) :: 0 :: 1 :: Nil foreach {
        i => {
          x() = i * 100
          y() = i * 30
          Thread.sleep(Math.abs(i) * 1000)
        }
      }
    }
  }).start()
  view.addListener(new InputListener {
    override def touchDown(event: InputEvent, xx: Float, yy: Float, pointer: Int, button: Int): Boolean = {
      x() = xx
      y() = yy
      super.touchDown(event, xx, yy, pointer, button)
      true
    }
  })
  root.add(new RLabel(skin,easedX.map("%.0f".format(_))))
  token.addAction(repeat(100,sequence(color(Color.WHITE,1),color(Color.BLACK,1))))
  //TODO flushing action..
  //so you need a task system for colors...

}
