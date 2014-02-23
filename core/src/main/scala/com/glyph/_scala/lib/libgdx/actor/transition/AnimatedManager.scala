package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import AnimatedManager._
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.glyph._scala.lib.libgdx.actor.table.Layers
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.util.extraction.Extractable
import scala.collection.mutable.ArrayBuffer
import com.glyph._scala.lib.libgdx.actor.AnimatedTable


class AnimatedManager
(builderMap: AnimatedGraph)
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
  type AnimatedGraph = Map[AnimatedConstructor, Map[String, (AnimatedActor => Unit, AnimatedConstructor)]]
  type TransitionMethod = AnimatedActor => Unit
}

trait LoadingAnimation[E[_], T] extends AnimatedExtractor[E, T] {
  val loadingAnimation: AnimatedActor

  override def onExtractionComplete(): Unit = {
    super.onExtractionComplete()
    if (getChildren.contains(loadingAnimation, true)) {
      out(loadingAnimation)(() => {})
    }
  }

  override def in(cb: () => Unit): Unit = {
    if (!extractable.isExtracted(target)) {
      in(loadingAnimation)(() => {
        //start loading after animation
        super.in(cb)
      })
    } else {
      super.in(cb)
    }
  }

  override def resume(cb: () => Unit): Unit = {
    if (!extractable.isExtracted(target)) {
      resume(loadingAnimation)(() => {
        super.resume(cb)
      })
    } else {
      super.resume(cb)
    }
  }

  override def out(cb: () => Unit): Unit = {
    if (getChildren.contains(loadingAnimation, true)) {
      out(loadingAnimation)(() => {})
    }
    super.out(cb)
  }
}

import scalaz._
import Scalaz._

trait MonadicAnimated[T] extends Animated {
  val holder: AnimatedActorHolder
  val animation: Actor with Animated
  def map[R](f: T => R): MonadicAnimated[R]
  def flatMap[R](f: T => MonadicAnimated[R]): MonadicAnimated[R]
  def addOnComplete(onComplete: T => Unit)
}

object MonadicAnimated{
  def extract[E:Extractable,T]
  (target:E[T])
  (animation:Actor with Animated)
  (holder:AnimatedActorHolder):MonadicAnimated[T] = {
    val empty = new EmptyOne[E,T](holder)(animation)
    empty.target = target
    empty
  }
}

class EmptyOne[E: Extractable, T]
(override val holder: AnimatedActorHolder)
(override val animation:Actor with Animated)
  extends MonadicAnimated[T] {
  val extractor = implicitly[Extractable[E]]
  var target: E[T] = null
  val callbacks = new ArrayBuffer[T=>Unit]()
  var extracting = false

  def extract[F:Extractable,R](target:F[R])(callback:R=>Unit){
    val extractor = implicitly[Extractable[F]]
    if (!extractor.isExtracted(target) && extracting) {
      holder.in(animation)(() => {
        extractor.extract(target)(result => {
          callback(result)
          out(()=>{})
        })
      })
    }else if (extractor.isExtracted(target)){
      extractor.extract(target)(result =>{
        callback(result)
        //no in , no out
      })
    }else{//not extracted, but extracting
      throw new IllegalStateException()
      // /do nothing but waiting
    }
  }
  def in(cb: () => Unit) {
    extract(target)(result=>{
      onComplete(result)
      cb()
    })
  }


  def out(cb: () => Unit): Unit = {
    if(holder.getChildren.contains(animation,true)){
      holder.out(animation)(()=>{})
    }
  }


  def pause(cb: () => Unit): Unit = {
    if(holder.getChildren.contains(animation,true)){
      holder.pause(animation)(()=>{})
    }
  }


  def resume(cb: () => Unit): Unit = {
    holder.resume(animation)(()=>{})
  }

  def onComplete(result: T){
    callbacks foreach (_(result))
  }


  def addOnComplete(onComplete: (T) => Unit): Unit = callbacks += onComplete

  override def map[R](f: (T) => R): MonadicAnimated[R] = {
    val empty = new EmptyOne[E, R](holder)(animation)
    addOnComplete(t => empty.onComplete(f(t)))
    empty
  }

  override def flatMap[R](f: (T) => MonadicAnimated[R]): MonadicAnimated[R] = {
    val empty = new EmptyOne[E, R](holder)(animation){
      override def in(cb: () => Unit): Unit = {
        extract(target)(res1=>{
          val animated = f(res1)
          animated.addOnComplete(res2 =>{
            onComplete(res2)
          })
          holder.in(animated.animation)(cb)
        })
      }
    }
    empty
  }
}

class AnimatedExtractor[E[_], T](info: Info, callbacks: Callbacks, val target: E[T], mapper: T => AnimatedConstructor)(implicit val assets: AssetManager, val extractable: Extractable[E])
  extends Layers
  with Animated
  with AnimatedActorHolder {
  var extracting = false
  var constructed: AnimatedActor = null

  def onExtractionComplete() {}

  override def in(cb: () => Unit): Unit = {
    extracting = true
    extractable.extract(target)(constructor => {
      if (extracting) {
        onExtractionComplete()
        constructed = mapper(constructor)(info)(callbacks)
        System.gc()
        in(constructed)(cb)
      }
      extracting = false
    })
  }

  override def out(cb: () => Unit): Unit = {
    extracting = false
    if (constructed != null) {
      out(constructed)(cb)
    } else {
      cb()
    }
  }

  override def pause(cb: () => Unit): Unit = {
    extracting = false
    if (constructed != null) {
      pause(constructed)(cb)
    } else {
      cb()

    }
  }

  override def resume(cb: () => Unit): Unit = {
    extracting = true
    extractable.extract(target)(constructor => {
      if (extracting) {
        onExtractionComplete()
        constructed = mapper(constructor)(info)(callbacks)
        in(constructed)(cb)
      }
    })
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