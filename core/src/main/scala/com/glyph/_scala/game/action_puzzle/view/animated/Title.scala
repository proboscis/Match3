package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui._
import com.glyph._scala.lib.util.{Animated, Logging}
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.language.existentials
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.glyph._scala.lib.libgdx.actor.action.ActionOps
import scala.collection.mutable
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.{AnimatedConstructor, Callbacks, Info}
import com.glyph._scala.lib.libgdx.{Builder, GLFuture, GdxUtil}
import com.glyph._scala.lib.util.updatable.task.Delay
import com.glyph._scala.lib.libgdx.actor.table.Layers
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, AnimatedTable}
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.test.MockTransition
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.injection.GLExecutionContext
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.game.builders.AnimatedConstructors

object Title {
  import Interpolation._
  import com.glyph._scala.game.builders.Builders._
  implicit val glContext = GLExecutionContext.context
  def second = for(font <- GLFuture(FontUtil.internalFont("font/corbert.ttf",30))) yield{
    roundRectTexture map{
      case round =>  info => callbacks => new AnimatedTable{
        val actor = new SpriteActor(round)
        add(actor).fill.expand
      }
    }:Builder[AnimatedConstructor]
  }
  def apply(labelConstructor: String => Actor): Info => Callbacks => Actor with Animated =
    info =>
      callbacks =>
        new WidgetGroup with Animated with Logging with Layers {
          log("title is created")
          val table = new Table
          table.debug()
          addActor(table)
          val label = labelConstructor((info.get("name") | "Title").toString) <| addActor
          val cell = table.add() <| (_.fill.expand.row)

          def animation(cb: () => Unit) {
            label.clearActions()
            label.addAction(
              sequence(
                ActionOps.run(() => {
                  label.setPosition(400, 400)
                }),
                moveTo(cell.getWidgetX, cell.getWidgetY, 0.3f, exp10Out),
                delay(0.2f),
                ActionOps.run(() => {
                  cb()
                  GdxUtil.post{// you must post!!! this bug is so hard to find the cause...
                    for(cb <- callbacks.get("dummy")){
                      cb(Map())
                    }
                  }
                })
              )
            )
          }

          override def layout(): Unit = {
            super.layout()
            table.setSize(getWidth, getHeight)
            table.layout()
          }

          def in(cb: () => Unit): Unit = animation(cb)

          def out(cb: () => Unit) {
            label.clearActions()
            label.addAction(
              sequence(
                moveTo(-400, 400, 0.3f, exp10Out),
                ActionOps.run(cb)
              )
            )
          }

          def pause(cb: () => Unit): Unit = out(cb)

          def resume(cb: () => Unit): Unit = animation(cb)
        }
}
class TitleTest extends MockTransition{
  //beware of second asset manager!
  override implicit def assetManager: AssetManager = new AssetManager()
  private implicit val _1 = builderExtractor
  private implicit val _2 = functionExtractor
  AnimatedConstructors.extract(Title.second)()
  override def graph: AnimatedManager.AnimatedGraph = {
    super.graph + Map()
  }

  manager.start(title,Map(),holder.push)
}

class WaitCallback(onComplete: () => Unit) {
  val callbackFlag = new mutable.HashMap[AnyRef, Boolean]() withDefault (_ => false)

  def token[P, R](f: P => R): P => R = param => {
    callbackFlag(f) = true
    if (callbackFlag.values.forall(identity)) {
      onComplete
    }
    f(param)
  }
}