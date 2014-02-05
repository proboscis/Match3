package com.glyph._scala.lib.libgdx.actor.table

import com.glyph._scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.libgdx.{BuilderExtractor, Builder}
import com.glyph._scala.lib.libgdx.actor.Tasking
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.util.updatable.task.{Do, Sequence}
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.{AnimatedActor, AnimatedBuilder}


trait AnimatedBuilderHolder2
  extends Layers
  with Tasking
  with BuilderExtractor {
  val builderStack = collection.mutable.Stack[AnimatedBuilder]()
  var currentAnimated: Actor with Animated = null

  def push(builder: AnimatedBuilder)(implicit am: AssetManager) {
    pauseCurrent()
    inBuilder(builder)
    builderStack.push(builder)
  }

  def pop()(implicit am: AssetManager) {
    outCurrent()
    if (!builderStack.isEmpty) {
      builderStack.pop()
      if (!builderStack.isEmpty) {
        resumeBuilder(builderStack.pop())
      }
    }
  }

  def switch(builder: AnimatedBuilder)(implicit am: AssetManager) {
    if (!builderStack.isEmpty) {
      outCurrent()
      inBuilder(builder)
      builderStack.pop()
      builderStack.push(builder)
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
    }else{
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

  protected def inBuilder(builder: AnimatedBuilder)(implicit am :AssetManager) {
    extract(builder) {
      animated =>
        currentAnimated = animated
        addActor(currentAnimated)
        animated.in(() => {
          log(infoText)
        })
    }(log)
  }
  protected def resumeBuilder(builder:AnimatedBuilder)(implicit am : AssetManager){
    extract(builder){
      animated =>
        currentAnimated = animated
        addActor(currentAnimated)
        animated.resume(()=>{
          log(infoText)
        })
    }(log)
  }

  def infoText: String = s"AnimatedBuilderHolder2:$builderStack , ${"%x".format(currentAnimated.hashCode())}"
}


object AnimatedBuilderHolder {
  type AnimatedActor = Actor with Animated
  type AnimatedBuilder = Builder[AnimatedActor]
}