package com.glyph.scala.lib.util.lifting

import com.glyph.scala.lib.util.reactive.{Varying, Var}

trait Clamp[T] extends Var[T] {
  private var min: Option[Varying[T]] = None
  private var max: Option[Varying[T]] = None
  private var evidence: Option[Numeric[T]] = None
  def clamp(min: Varying[T], max: Varying[T])(implicit ev: Numeric[T]):this.type = {
    this.min = Some(min)
    this.max = Some(max)
    this.evidence = Some(ev)
    this
  }

  override def update(v: T) {
    //println(min,max,evidence,v)
    super.update((for {
      min <- this.min
      max <- this.max
      ev <- evidence
    } yield ev.max(min(), ev.min(v, max()))).getOrElse(v))
  }

  override def update(f: (T) => T): Unit = {
    val v = f(current)
    super.update(
      (for {
        min <- this.min
        max <- this.max
        ev <- evidence
      } yield ev.max(min(), ev.min(v, max()))).getOrElse(v))
  }
}