package com.glyph.scala.lib.util.json

import com.glyph.scala.lib.util.reactive.Varying
import scala.language.dynamics

/**
 * @author glyph
 */
object DepreactedRJSON {
  def apply(source: Varying[String]): DepreactedRJSON = {
    new DepreactedRJSON(source map DeprecatedJSON.parseJSON)
  }

  sealed class IBDS[T]

  implicit object I extends IBDS[Int]

  implicit object B extends IBDS[Boolean]

  implicit object D extends IBDS[Double]

  implicit object S extends IBDS[String]

  implicit object F extends IBDS[Float]

}

class DepreactedRJSON(o: Varying[ScalaJSON]) extends Dynamic {
  def toStr: Varying[String] = o map {
    _.toString
  }

  def toInt: Varying[Int] = o map {
    _.toInt
  }

  def toDouble: Varying[Double] = o map {
    _.toDouble
  }

  def toBoolean: Varying[Boolean] = o map {
    _.toBoolean
  }

  /*
  def to[T]:Varying[T] = o->{

  }
  */

  def apply(key: String): DepreactedRJSON = new DepreactedRJSON(o map {
    _.apply(key)
  })

  def apply(idx: Int): DepreactedRJSON = new DepreactedRJSON(o map {
    _.apply(idx)
  })

  def selectDynamic(name: String): DepreactedRJSON = apply(name)

  def applyDynamic(name: String)(arg: Any): DepreactedRJSON = {
    arg match {
      case s: String => apply(name)(s)
      case n: Int => apply(name)(n)
      case u: Unit => apply(name)
    }
  }

  import DepreactedRJSON._

  def to[T: IBDS]: Varying[T] = {
    implicitly[IBDS[T]] match {
      case I => toInt.asInstanceOf[Varying[T]]
      case B => toBoolean.asInstanceOf[Varying[T]]
      case D => toDouble.asInstanceOf[Varying[T]]
      case S => toStr.asInstanceOf[Varying[T]]
      case F => o.map {
        _.toDouble.toFloat
      }.asInstanceOf[Varying[T]]
    }
  }
}