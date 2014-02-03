package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, WidgetGroup, Label, Skin}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.glyph.scala.lib.util.{Logging, Animated}
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.libgdx.actor.Tasking

/**
 * @author glyph
 */
class AnimatedHolderTest extends ScreenBuilder {
  def requirements: Set[(Class[_], Seq[String])] = AnimatedHolderTest.skinResource

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    debug() = true
    import AnimatedHolderTest._

    val skin = "skin/holo/Holo-dark-xhdpi.json".fromAssets[Skin]
    val holder = new AnimatedBuilderHolder {}
    val loadingLabel = new Label("Loading", skin)
    stage.addListener(new InputListener {

      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
        super.touchDown(event, x, y, pointer, button)
        if (x < stage.getWidth / 2) holder.pop() else holder.push(builder)
        true
      }

    })
    root.add(holder).fill.expand()
  }
}

object AnimatedHolderTest {
  val skinResource: Builder.Assets = Set(classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json"))
  val builder = new Builder[Actor with Animated] with Logging {
    def requirements: Set[(Class[_], Seq[String])] = AnimatedHolderTest.skinResource

    def create(implicit assets: AssetManager): Actor with Animated = new WidgetGroup with Animated with Tasking {
      val skin = "skin/holo/Holo-dark-xhdpi.json".fromAssets[Skin]
      val table = new Table
      addActor(table)
      val labels = 1 to 5 map (i => new Label(i.toString, skin))
      labels foreach addActor
      val pairs = labels map {
        l => val cell = table.add()
          cell.fill.expand().row()
          l -> cell
      }

      val delayTime = 0.032f
      import scala.language.existentials
      def come(cb: () => Unit)(right: Boolean) {
        table.setSize(getWidth, getHeight)
        table.layout()
        val par = new Parallel
        pairs.zipWithIndex.foreach {
          case t@(pair, i) =>
            val (label, cell) = pair
            val seq = Sequence()
            val it = new Interpolator[Actor]
            label.setPosition(if (right) getWidth else -getWidth, cell.getWidgetY)
            seq.add(Delay(i * delayTime))
            seq.add(it set label of ActorAccessor.XY to(cell.getWidgetX, cell.getWidgetY) in 0.3f using Interpolation.exp10Out)
            par.add(seq)
        }
        add(Sequence(par, Do(cb)))
      }

      def go(cb: () => Unit)(right: Boolean) {
        table.setSize(getWidth, getHeight)
        table.layout()
        val par = new Parallel
        pairs.zipWithIndex.foreach {
          case t@(pair, i) =>
            val (label, cell) = pair
            val seq = Sequence()
            val it = new Interpolator[Actor]
            seq.add(Delay(i * delayTime))
            seq.add(it set label of ActorAccessor.XY to(if (right) getWidth else -getWidth, cell.getWidgetY) in 0.3f using Interpolation.exp10Out)
            seq.add(Block {
              log(i + " done")
            })
            par.add(seq)
        }
        add(Sequence(par, Do(cb)))
      }

      //table.debug()

      def in(cb: () => Unit): Unit = come(cb)(true)

      def out(cb: () => Unit): Unit = go(cb)(true)

      def pause(cb: () => Unit): Unit = go(cb)(false)

      def resume(cb: () => Unit): Unit = come(cb)(false)
    }
  }
}
