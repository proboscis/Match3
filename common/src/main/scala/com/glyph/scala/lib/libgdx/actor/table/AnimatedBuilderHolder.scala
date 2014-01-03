package com.glyph.scala.lib.libgdx.actor.table

import com.glyph.scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.Builder
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.updatable.task.{Do, Sequence}

/**
 * @author glyph
 */
trait AnimatedBuilderHolder
  extends ActorHolder
  with Tasking
  with TaskWaiter
  with Logging {
  import AnimatedBuilderHolder._

  val builderStack = collection.mutable.Stack[AnimatedBuilder]()
  var currentActor: AnimatedActor = null
  var currentBuilder: AnimatedBuilder = null

  override def addActor(actor: Actor) {
    super.addActor(actor)
    setSizeOfChildren()
  }
  //TODO check states!
  def push(builder: AnimatedBuilder)(implicit am: AssetManager) {
    if (currentActor != null) {
      val c = currentActor
      c.pause(() => {
        c.remove()
        log(builderStack,currentActor)

      })
      builderStack.push(currentBuilder)
    }
    currentBuilder = builder
    val cb = currentBuilder
    add(Sequence(
      new AssetTask(cb.requirements)(log),
      Do {
        val view = cb.create
        currentActor = view
        addActor(view)
        view.in(() => {
          log(builderStack,currentActor)
        })
      }))
  }

  def pop()(implicit am: AssetManager) {
    if (currentActor != null) {
      val c = currentActor
      c.out(() => {
        c.remove()
        log(builderStack,currentActor)
      })
      currentActor = null
    }
    if (!builderStack.isEmpty) {
      val prev = builderStack.pop()
      currentBuilder = prev
      add(Sequence(
        new AssetTask(prev.requirements)(log),
        Do {
          val view = prev.create
          addActor(view)
          currentActor = view
          view.resume(() => {
            log(builderStack,currentActor)
          })
        }
      ))
    }
  }
}

object AnimatedBuilderHolder {
  type AnimatedActor = Actor with Animated
  type AnimatedBuilder = Builder[AnimatedActor]
}