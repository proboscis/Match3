package com.glyph.scala.lib.libgdx

import com.glyph.scala.lib.util.updatable.task._
import com.glyph.scala.lib.libgdx.actor.table.AssetTask
import com.badlogic.gdx.assets.AssetManager

/**
 * @author glyph
 */
trait BuilderExtractor extends ParallelProcessor {
  import scalaz._
  import Scalaz._
  def extract[T](builder: Builder[T])(onComplete: T => Unit)(progress:Float => Unit)(implicit assets: AssetManager): Task = Sequence(
    new AssetTask(builder.requirements)(progress),
    Block {
      onComplete(builder.create)
    }
  ) <| add
}
