package com.glyph.scala.lib.libgdx.actor.transition

import com.glyph.scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor
import AnimatedManager._
import com.glyph.scala.lib.libgdx.Builder
import com.glyph.scala.lib.libgdx.actor.table.{AssetTask, AnimatedBuilderHolder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.actor.Tasking
import com.glyph.scala.lib.util.updatable.task.{ParallelProcessor, Task, Do, Sequence}
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor

/**
 * i dont want the screen to care anything about transitioning.?
 */
/**
 * well, this is useless since this forces some class to manage whole scene transitions!
 * @param builderMap
 * @param assets
 */
class AnimatedManager
(builderMap: Map[Builder[AnimatedConstructor] ,Map [String,(Builder[AnimatedActor]=>Unit,Builder[AnimatedConstructor])]])
(implicit assets: AssetManager)
  extends BuilderExtractor
  {
  def init(builder: Builder[AnimatedConstructor],info:Info,transit:Builder[AnimatedActor]=>Unit) {
    val callbacks = builderMap(builder).mapValues{
      case (transitioner,constructorBuilder) =>
        (info:Info) =>init(constructorBuilder,info,transitioner)
    }
    val animatedBuilder = builder map (_(info)(callbacks))
    transit(animatedBuilder)
  }
}

object AnimatedManager {
  type Info = String Map Any
  type Callback = Info => Unit
  type Callbacks = String Map Callback
  type AnimatedConstructor = Info => Callbacks => Actor with Animated
}

trait BuilderExtractor extends ParallelProcessor {
  import scalaz._
  import Scalaz._


  def extract[T](builder: Builder[T])(onComplete: T => Unit)(progress:Float => Unit)(implicit assets: AssetManager): Task = Sequence(
    new AssetTask(builder.requirements)(progress),
    Do {
      onComplete(builder.create)
    }
  ) <| add
}