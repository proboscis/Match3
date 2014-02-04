package com.glyph.scala.lib.libgdx

import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.libgdx.actor.table.AssetTask
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.extraction.Extractable
import com.glyph.scala.game.Glyphs
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

  override def isExtracted[T](target: Builder[T]): Boolean = am.isLoaded(target.requirements)
}