package com.glyph._scala.game

import com.glyph._scala.lib.libgdx.GdxOps
import com.glyph._scala.lib.util.GlyphLibOps
import scala.util.Try
import scalaz._
import Scalaz._
import scala.concurrent.Future

/**
 * @author glyph
 */
object Glyphs
  extends GdxOps
  with GlyphLibOps {

  implicit class TryOps[T](val t: Try[T]) extends AnyVal {
    def toVnel: ValidationNel[Throwable, T] = {
      t match {
        case util.Success(s) => s.successNel
        case util.Failure(f) => f.failNel
      }
    }
  }

  implicit class FutureOps[T](val f: Future[T]) extends AnyVal {
    def valueVnel: ValidationNel[Throwable, T] = {
      f.value match {
        case Some(t) => t.toVnel
        case None => new RuntimeException("unknown error of future...").failNel
      }
    }
  }

  implicit class ValidationThrowableOps[V](val vnel: ValidationNel[Throwable, ValidationNel[Throwable, V]]) extends AnyVal {
    def flatten = vnel.flatMap(identity)
  }

}
