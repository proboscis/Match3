package com.glyph.scala.lib.libgdx.actor.transition

import com.glyph.scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import AnimatedManager._
import com.glyph.scala.lib.libgdx.{BuilderExtractor, Builder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.glyph.scala.lib.libgdx.actor.table.Layers
import com.glyph.scala.lib.util.updatable.task.Task
import com.glyph.scala.game.Glyphs
import Glyphs._


//TODO make this manager don't handle the builder and initializer and let the animated itself to handle it.
class AnimatedManager
(builderMap: Map[AnimatedConstructor, Map[String, (AnimatedActor => Unit, AnimatedConstructor)]])
(implicit assets: AssetManager) {
  val builders = builderMap withDefaultValue Map()

  def start(builder: AnimatedConstructor, info: Info, transit: AnimatedActor => Unit) {
    val callbacks: Map[String, Info => Unit] = builders(builder).mapValues {
      case (transitioner, constructorBuilder) =>
        (info: Info) => start(constructorBuilder, info, transitioner)
    }.withDefault(_ => (info: Info) => Unit)
    val animated = builder(info)(callbacks)
    transit(animated)
  }
}

object AnimatedManager {
  type Info = String Map Any
  type Callback = Info => Unit
  type Callbacks = String Map Callback
  type AnimatedConstructor = Info => Callbacks => Actor with Animated
}


trait LoadingAnimation extends AnimatedBuilderExtractor {
  val loadingAnimation: AnimatedActor

  override def onExtractionComplete(): Unit = {
    super.onExtractionComplete()
    if (getChildren.contains(loadingAnimation, true)) {
      out(loadingAnimation)(()=>{})
    }
  }

  override def in(cb: () => Unit): Unit = {
    if (!builder.isReady) {
      in(loadingAnimation)(() => {})
    }else{
      import scala.collection.JavaConversions._
      assets.getAssetNames.iterator() foreach log
    }
    super.in(cb)
  }

  override def resume(cb: () => Unit): Unit = {
    if (!builder.isReady) {
      resume(loadingAnimation)(() => {})
    }
    super.resume(cb)
  }

  override def out(cb: () => Unit): Unit = {
    if (getChildren.contains(loadingAnimation, true)) {
      out(loadingAnimation)(()=>{})
    }
    super.out(cb)
  }
}

class AnimatedBuilderExtractor(info: Info, callbacks: Callbacks, val builder: Builder[AnimatedConstructor])(implicit val assets: AssetManager)
  extends Layers
  with Animated
  with Tasking
  with BuilderExtractor
  with AnimatedActorHolder {
  var extractTask: Task = null
  var constructed: AnimatedActor = null

  def onExtractionComplete() {}

  override def in(cb: () => Unit): Unit = {
    extractTask = extract(builder)(constructor => {
      onExtractionComplete()
      constructed = constructor(info)(callbacks)
      in(constructed)(cb)
    })(log)
  }


  override def out(cb: () => Unit): Unit = {
    if (constructed != null) {
      out(constructed)(cb)
    } else {
      if (extractTask != null && !extractTask.isCompleted) {
        extractTask.cancel()
        cb()
      } else {
        cb()
      }
    }
  }

  override def pause(cb: () => Unit): Unit = {
    if (constructed != null) {
      pause(constructed)(cb)
    } else {
      if (extractTask != null && !extractTask.isCompleted) {
        extractTask.cancel()
        cb()
      } else {
        cb()
      }
    }
  }

  override def resume(cb: () => Unit): Unit = {
    extractTask = extract(builder)(constructor => {
      onExtractionComplete()
      constructed = constructor(info)(callbacks)
      in(constructed)(cb)
    })(log)
  }
}

trait AnimatedActorHolder extends Layers {
  def checkExistance(actor: Actor) = if (!this.getChildren.contains(actor, true)) addActor(actor)

  def in(animated: AnimatedActor)(cb: () => Unit) {
    checkExistance(animated)
    animated.in(cb)
  }

  def out(animated: AnimatedActor)(cb: () => Unit) {
    checkExistance(animated)
    animated.out(() => {
      animated.remove()
      cb()
    })
  }

  def pause(animated: AnimatedActor)(cb: () => Unit) {
    checkExistance(animated)
    animated.pause(() => {
      animated.remove()
      cb()
    })
  }

  def resume(animated: AnimatedActor)(cb: () => Unit) {
    checkExistance(animated)
    animated.resume(cb)
  }

  def switch(prev: AnimatedActor, next: AnimatedActor)(outCb: () => Unit, inCb: () => Unit) {
    out(prev)(outCb)
    in(next)(inCb)
  }
}

trait StackedAnimatedActorHolder extends AnimatedActorHolder with Logging {
  val builderStack = collection.mutable.Stack[AnimatedActor]()
  var currentAnimated: Actor with Animated = null

  def push(animated: AnimatedActor) {
    pauseCurrent()
    inBuilder(animated)
    builderStack.push(animated)
  }

  def pop() {
    outCurrent()
    if (!builderStack.isEmpty) {
      builderStack.pop()
      if (!builderStack.isEmpty) {
        resumeBuilder(builderStack.pop())
      }
    }
  }

  def switch(animated: AnimatedActor) {
    if (!builderStack.isEmpty) {
      outCurrent()
      inBuilder(animated)
      builderStack.pop()
      builderStack.push(animated)
    }
  }

  protected def pauseCurrent() {
    log("pause current")
    if (currentAnimated != null) {
      log("current animated is not null")
      val view = currentAnimated
      view.pause(() => {
        log("pause finish")
        view.remove()
      })
    } else {
      log("what a hell?")
    }
  }

  protected def outCurrent() {
    if (currentAnimated != null) {
      val view = currentAnimated
      view.out(() => {
        view.remove()
      })
    }
  }

  protected def inBuilder(animated: AnimatedActor) {
    currentAnimated = animated
    addActor(currentAnimated)
    animated.in(() => {
      log(infoText)
    })
  }

  protected def resumeBuilder(animated: AnimatedActor) {
    currentAnimated = animated
    addActor(currentAnimated)
    animated.resume(() => {
      log(infoText)
    })
  }

  def infoText: String = s"AnimatedBuilderHolder2:$builderStack , ${"%x".format(currentAnimated.hashCode())}"
}