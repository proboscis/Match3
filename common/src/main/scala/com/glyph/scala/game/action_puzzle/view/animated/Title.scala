package com.glyph.scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui._
import com.glyph.scala.lib.util.{Animated, Logging}
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.language.existentials
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.glyph.scala.lib.libgdx.actor.action.ActionOps
import scala.collection.mutable
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.{Callbacks, Info}
import com.glyph.scala.lib.libgdx.GdxUtil

object Title {

  import Interpolation._

  def apply(labelConstructor: String => Actor): Info => Callbacks => Actor with Animated =
    info =>
      callbacks =>
        new WidgetGroup with Animated with Logging {
          log("title is created")
          val table = new Table
          table.debug()
          addActor(table)
          val label = labelConstructor((info.get("name") | "name is not set").toString) <| addActor
          val cell = table.add() <| (_.fill.expand.row)

          def animation(cb: () => Unit) {
            label.clearActions()
            label.addAction(
              sequence(
                ActionOps.run(() => {
                  label.setPosition(400, 400)
                }),
                moveTo(cell.getWidgetX, cell.getWidgetY, 0.3f, exp10Out),
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