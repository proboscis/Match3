package com.glyph.scala.lib.libgdx.actor.table

import com.glyph.scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.Builder
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.updatable.task.{Do, Sequence}
import com.glyph.scala.lib.libgdx.actor.transition.BuilderExtractor
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.{AnimatedActor, AnimatedBuilder}

/**
 * @author glyph
 */
trait AnimatedBuilderHolder
  extends ActorHolder
  with Tasking
  with TaskWaiter
  with Logging
  with BuilderExtractor{

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
    extract(currentBuilder){
      view => currentActor = view
        addActor(view)
        view.in(() => {
          log(builderStack,currentActor)
        })
    }(log)
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
      extract(prev){
        view =>
          addActor(view)
          currentActor = view
          view.resume(() => {
            log(builderStack,currentActor)
          })
      }(log)
    }
  }
  def switch(builder:AnimatedBuilder)(implicit am:AssetManager){
  }
  def pauseCurrent(){
    if(currentActor != null){
      val c = currentActor
      c.pause(() => {
        c.remove()
        log(builderStack,currentActor)
      })
      builderStack.push(currentBuilder)
    }
  }
}

trait AnimatedBuilderHolder2
  extends ActorHolder
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
    if (currentAnimated != null) {
      val view = currentAnimated
      view.pause(() => {
        view.remove()
      })
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

  def infoText: String = s"AnimatedBuilderHolder2:$builderStack , $currentAnimated"
}


object AnimatedBuilderHolder {
  type AnimatedActor = Actor with Animated
  type AnimatedBuilder = Builder[AnimatedActor]
}