package com.glyph._scala.test

import com.glyph._scala.game.action_puzzle.view.animated.LazyAssets
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.lib.libgdx.gl.ShaderUtil
import com.glyph._scala.game.Glyphs._
import com.glyph._scala.lib.libgdx.Builder
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.{Batch, BitmapFont}
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Label}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter
import com.badlogic.gdx.scenes.scene2d.{Event, Touchable, InputEvent, Actor}
import com.badlogic.gdx.input.GestureDetector.{GestureAdapter, GestureListener}
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.math.MathUtils
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class DistanceFieldTest extends MockTransition with LazyAssets {
  val shader = ShaderUtil.load("shader/dist.vert", "shader/dist.frag")
  val fontParams = new BitmapFontParameter <|{
    p =>
      p.minFilter = TextureFilter.Linear
      p.magFilter = TextureFilter.Linear
  }
  trait FillX extends Label with Logging{
    def getTextWidth = getStyle.font.getMultiLineBounds(getText).width
    def calcFontScale(){
      val scale = getWidth/getTextWidth
      setFontScale(scale)
      err("setFontScale",scale)
    }
    override def setText(newText: CharSequence): Unit = {
      super.setText(newText)
      calcFontScale()
    }

    override def sizeChanged(): Unit = {
      super.sizeChanged()
      calcFontScale()
    }
  }
  val screen = shader.map(_.map(_.map(sp => (Builder("font/code_120.fnt",fontParams)&Builder("font/code_dist.fnt",fontParams)).map {
    case font&dist =>
      val label1 = new Label("RESULT\nA", new LabelStyle(dist, Color.WHITE)) with Logging with FillX{

        override def draw(batch: Batch, parentAlpha: Float): Unit = {
          batch.setShader(sp)
          super.draw(batch, parentAlpha)
          batch.setShader(null)
        }
      }
      val label2 = new Label("RESULT\nSCORE",new LabelStyle(font,Color.WHITE)) with FillX
      label1::label2::Nil foreach{
        l => l.setTouchable(Touchable.disabled)
          l.setFontScale(1.0f)
      }
    new Table{
      override def sizeChanged(): Unit = {
        super.sizeChanged()
        setBounds(getX,getY,getWidth,getHeight)
      }
    } <| {
      t =>
        label1::label2::Nil foreach (t.add(_).fill.expand.row())
        t.addListener(new ActorGestureListener{

          override def handle(e: Event): Boolean = super.handle(e);true

          override def zoom(event: InputEvent, initialDistance: Float, distance: Float): Unit = {
            super.zoom(event, initialDistance, distance)
            label1::label2::Nil foreach{
              label => val scale = label.getFontScaleX*distance/initialDistance
                log("zooming %.2f %.2f".format(initialDistance,distance))
                log("scale: %.2f".format(scale))
                label.setFontScale(MathUtils.clamp(scale,0.1f,50f))
            }
          }
        })
        t.setTouchable(Touchable.enabled)
        t.setFillParent(true)
    }
  }
  )))

  override def graph: AnimatedManager.AnimatedGraph = super.graph

  manager.start(screen, Map(), holder.push)
}