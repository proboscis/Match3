package com.glyph._scala.lib.libgdx

import com.glyph._scala.lib.util.updatable.task._
import com.glyph._scala.lib.libgdx.actor.table.AssetTask
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.util.extraction.Extractable
import com.glyph._scala.game.Glyphs
import Glyphs._
import scala.util.{Success, Try}
import scala.annotation.target
import scala.annotation
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
trait BuilderExtractor extends ParallelProcessor {
  import scalaz._
  import Scalaz._
  def extract[T](builder: Builder[T])(onComplete: T => Unit)(progress: Float => Unit)(implicit assets: AssetManager): Task = Sequence(
    new AssetTask(builder.requirements)(progress),
    Block {
      onComplete(builder.create)
    }
  ) <| addTask
}

class BuilderExtractor2(implicit processor: TaskProcessor, am: AssetManager) extends Extractable[Builder] with Logging{
  override def extract[T](target: Builder[T])(callback: Try[T] => Unit){
    log("start extraction")
    processor.addTask(
      Sequence(
        new AssetTask(
          target.requirements)(_ => {}),
        Block {
          log("finished extraction")
          callback(Success(target.create))
          //TODO This never returns Failure even if the  animation is canceled...
        }
      )
    )
  }

  override def isExtracted[T](target: Builder[T]): Boolean = {
    assert(am != null)
    am.isLoaded(target.requirements)
  }

  override def map[A, B](fa: Builder[A])(f: (A) => B): Builder[B] = Builder(fa.requirements,(fa.create(_:AssetManager)).andThen(f))
}