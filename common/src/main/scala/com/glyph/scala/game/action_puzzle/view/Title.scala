package com.glyph.scala.game.action_puzzle.view

import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.{Animated, Logging}
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.Actor
import scala.language.existentials
import scalaz._
import Scalaz._
import com.badlogic.gdx.scenes.scene2d.actions.Actions._
import com.glyph.scala.lib.libgdx.actor.action.ActionOps
import scala.collection.mutable
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.{Callbacks, Info}

object Title {
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

          def animation(cb: () => Unit = () => {}) {
            label.clearActions()
            label.addAction(
              sequence(
                ActionOps.run(() => {
                  label.setPosition(400, 400)
                }),
                moveTo(cell.getWidgetX, cell.getWidgetY, 0.3f, Interpolation.exp10Out),
                ActionOps.run(()=>{
                  cb()
                  callbacks("dummy")(Map())
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

          def out(cb: () => Unit): Unit = animation(cb)

          def pause(cb: () => Unit): Unit = animation(cb)

          def resume(cb: () => Unit): Unit = animation(cb)
        }
}

class TitleBuilder extends Builder[Actor with Animated] {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )

  def create(implicit assets: AssetManager) = ???
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