package com.glyph._scala.lib.libgdx.actor.transition

import com.glyph._scala.lib.util.{Logging, Animated}
import com.badlogic.gdx.scenes.scene2d.Actor
import AnimatedManager._
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.glyph._scala.lib.libgdx.actor.table.Layers
import com.glyph._scala.lib.util.extraction.Extractable
import scala.collection.mutable.ArrayBuffer

import scalaz._
import Scalaz._
import scala.language.higherKinds
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import scala.util.Try

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

object AnimatedConstructor {
  def apply(actor: Actor): AnimatedConstructor = info => callbacks => new AnimatedTable <| (_.add(actor).fill.expand)
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


trait MAnimated[+T] extends AnimatedActorHolder with Animated {
  val animation: Actor with Animated

  def map[R](f: T => R): MAnimated[R]

  def flatMap[R](f: T => MAnimated[R]): MAnimated[R]

  def addOnComplete(onComplete: Try[T] => Unit)
}

object MAnimated extends Logging {
  def extract[E[_] : Extractable, T]
  (target: E[T])
  (animation: Actor with Animated): MAnimated[T] = {
    val empty = new EmptyOne[E, T](animation)
    empty.target = target
    empty
  }

  /**
   * this works, at least for now...
   * @param extractor
   * @return
   */
  def toAnimatedConstructor(extractor: MAnimated[AnimatedConstructor])(implicit errorHandler:Throwable=>AnimatedConstructor): AnimatedConstructor = info => callbacks => {
    new AnimatedActorHolder with Animated {
      var constructed: Actor with Animated = null
      override def in(cb: () => Unit): Unit = {
        extractor.addOnComplete(
          constructor => {
            log("summed extractable is extracted.")
            constructed = constructor.recover{case e => errorHandler(e)}.get(info)(callbacks)
            in(constructed)(cb)
          }
        )
        in(extractor)(() => {})
      }

      //TODO the code below won't work properly in certain conditions.
      def currentAnimated = if (constructed != null) constructed else extractor

      override def out(cb: () => Unit): Unit = currentAnimated |> (out(_)(cb))

      override def pause(cb: () => Unit): Unit = currentAnimated |> (pause(_)(cb))

      override def resume(cb: () => Unit): Unit = currentAnimated |> (resume(_)(cb))
    }
  }

  def toAnimatedActor(extractor: MAnimated[AnimatedActor])(implicit errorHandler:Throwable=>AnimatedActor): AnimatedActor =
    new AnimatedActorHolder with Animated {
      var constructed: Actor with Animated = null

      override def in(cb: () => Unit): Unit = {
        extractor.addOnComplete(
          animated => {
            log("MAnimated is done.")
            constructed = animated.recover{case e => errorHandler(e)}.get
            in(constructed)(cb)
          }
        )
        in(extractor)(() => {})
      }

      //TODO the code below won't work properly in certain conditions.
      def currentAnimated = if (constructed != null) constructed else extractor

      override def out(cb: () => Unit): Unit = currentAnimated |> (out(_)(cb))

      override def pause(cb: () => Unit): Unit = currentAnimated |> (pause(_)(cb))

      override def resume(cb: () => Unit): Unit = currentAnimated |> (resume(_)(cb))
    }
}

class EmptyOne[E[_] : Extractable, T]
(override val animation: Actor with Animated)
  extends MAnimated[T] with Logging {
  self =>
  val extractor = implicitly[Extractable[E]]
  var target: E[T] = null.asInstanceOf[E[T]]
  val callbacks = new ArrayBuffer[Try[T] => Unit]()
  var extracting = false

  def extract[F[_] : Extractable, R](target: F[R])(callback: Try[R] => Unit) {
    log("extract!")
    val extractor = implicitly[Extractable[F]]
    assert(target != null)
    if (!extractor.isExtracted(target) && !extracting) {
      extracting = true
      log("started animation because not extracted and extracting.")
      in(animation)(() => {
        log("start extraction")
        extractor.extract(target)(result => {
          log("finished extraction")
          callback(result)
          extracting = false
          out(() => {})
        })
      })
    } else if (extractor.isExtracted(target)) {
      log("already extracted, trying to extract without animation")
      extractor.extract(target)(result => {
        callback(result)
        //no in , no out
      })
    } else {
      log("not extracted yet, but extract is called while extracting")
      //not extracted, but extracting
      throw new IllegalStateException()
      // /do nothing but waiting
    }
  }

  def in(cb: () => Unit) {
    //this is called twice by someone...
    log("start in animation")
    extract(target)(result => {
      onComplete(result)
      cb()
    })
  }

  def out(cb: () => Unit): Unit = {
    if (getChildren.contains(animation, true)) {
      out(animation)(() => {})
    }
  }


  def pause(cb: () => Unit): Unit = {
    if (getChildren.contains(animation, true)) {
      pause(animation)(() => {})
    }
  }


  def resume(cb: () => Unit): Unit = {
    resume(animation)(() => {})
  }

  def onComplete(result: Try[T]) {
    callbacks foreach (_(result))
    callbacks.clear()
  }


  def addOnComplete(onComplete: Try[T] => Unit): Unit = callbacks += onComplete

  override def map[R](f: (T) => R): MAnimated[R] = {
    log("map")
    val empty = new EmptyOne[E, R](animation)
    empty.target = target.map(f)
    empty
  }

  override def flatMap[R](f: (T) => MAnimated[R]): MAnimated[R] = {
    log("flatMap")
    //problem is that you cannot combine two animation
    //i think i should split the visual effect and its effect as a monad.
    // i cant under staaaaand!
    val empty = new EmptyOne[E, R](animation) {
      second =>
      override def in(cb: () => Unit): Unit = {
        log("flat mapped in")
        second.extract(self.target){
            case scala.util.Success(r) =>log("second extracted")
              val animated = f(r)
              log("flattening target is : " + animated.getClass.getCanonicalName)
              animated.addOnComplete(res2 =>{
                log("second monadic animated is completed.")
                second.onComplete(res2)
              }
              )
              log("start second animation")
              second.in(animated)(() => {
                log("second animation done")
                cb
              })
            case scala.util.Failure(e) => errE("error while flatMapping:")(e)
          }
        }
      }
    empty
  }
}

class AnimatedExtractor[E[_], T](info: Info, callbacks: Callbacks, val target: E[T], mapper: T => AnimatedConstructor)
                                (implicit val assets: AssetManager, val extractable: Extractable[E],val errorHandler:Throwable=>AnimatedConstructor)
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
        constructed = constructor.map(mapper).recover{case e => errorHandler(e)}.get(info)(callbacks)
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
        constructed = constructor.map(mapper).recover{case e => errorHandler(e)}.get(info)(callbacks)
        in(constructed)(cb)
      }
    })
  }
}

trait AnimatedActorHolder extends Layers with Logging {
  def checkExistence(actor: Actor) = if (!this.getChildren.contains(actor, true)) addActor(actor)

  def in(animated: AnimatedActor)(cb: () => Unit) {
    log("AnimatedActorHolder:calling in of " + animated)
    log("hashCode:%x".format(animated.hashCode()))
    checkExistence(animated)
    animated.in(cb)
  }

  def out(animated: AnimatedActor)(cb: () => Unit) {
    checkExistence(animated)
    animated.out(() => {
      animated.remove()
      cb()
    })
  }

  def pause(animated: AnimatedActor)(cb: () => Unit) {
    checkExistence(animated)
    animated.pause(() => {
      animated.remove()
      cb()
    })
  }

  def resume(animated: AnimatedActor)(cb: () => Unit) {
    checkExistence(animated)
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