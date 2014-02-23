package com.glyph._scala.lib.libgdx

import com.glyph._scala.lib.util.updatable.task._
import com.glyph._scala.lib.libgdx.actor.table.AssetTask
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.lib.util.extraction.Extractable
import com.glyph._scala.game.Glyphs
import Glyphs._
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
  ) <| add
}

class BuilderExtractor2(implicit processor: TaskProcessor, am: AssetManager) extends Extractable[Builder] {
  override def extract[T](target: Builder[T])(callback: (T) => Unit): Unit =
    processor.add(
      Sequence(
        new AssetTask(
          target.requirements)(_ => {}),
        Block {
          callback(target.create)
        }
      )
    )

  override def isExtracted[T](target: Builder[T]): Boolean = {
    assert(am != null)
    am.isLoaded(target.requirements)
  }

  override def map[A, B](fa: Builder[A])(f: (A) => B): Builder[B] = Builder(fa.requirements,(fa.create(_:AssetManager)).andThen(f))
}