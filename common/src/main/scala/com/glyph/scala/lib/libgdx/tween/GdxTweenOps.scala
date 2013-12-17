package com.glyph.scala.lib.libgdx.tween

import com.glyph.scala.lib.util.updatable.task.tween.Accessor
import com.glyph.scala.lib.util.Logging
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.Color

/**
 * @author proboscis
 */
object Test extends GdxTweenOps

trait GdxTweenOps extends Logging {
  trait A[T]{
    def get(tgt:T):Float
    def set(tgt:T,v:Float)
  }
  trait AX[T] extends A[T]
  trait AY[T] extends A[T]
  trait AZ[T] extends A[T]
  trait AA[T] extends A[T]
  trait AR[T] extends A[T]
  trait AG[T] extends A[T]
  trait AB[T] extends A[T]
  trait XAccessor[T] extends Accessor[T]
  trait YAccessor[T] extends Accessor[T]
  trait ZAccessor[T] extends Accessor[T]
  trait XYAccessor[T] extends Accessor[T]
  trait XYZAccessor[T] extends Accessor[T]
  trait AAccessor[T] extends Accessor[T]
  trait RAccessor[T] extends Accessor[T]
  trait GAccessor[T] extends Accessor[T]
  trait BAccessor[T] extends Accessor[T]
  trait RGBAAccessor[T] extends Accessor[T]
  trait RGBAccessor[T] extends Accessor[T]
  def pimpedXAccessor[T: AX : ClassTag] = {
    log("created an XAccessor for:" + implicitly[ClassTag[T]])
    new XAccessor[T] {
      val ax = implicitly[AX[T]]

      def size: Int = 1

      def get(tgt: T, result: Array[Float]) {
        result(0) = ax.get(tgt)
      }

      def set(tgt: T, values: Array[Float]) {
        ax.set(tgt, values(0))
      }
    }
  }
  def pimpedYAccessor[T: AY : ClassTag] = {
    log("created an XAccessor for:" + implicitly[ClassTag[T]])
    new YAccessor[T] {
      val ay = implicitly[AY[T]]

      def size: Int = 1

      def get(tgt: T, result: Array[Float]) = result(0) = ay.get(tgt)

      def set(tgt: T, values: Array[Float]) = ay.set(tgt, values(0))
    }
  }
  def pimpedXYAccessor[T: AX : AY : ClassTag] = {
    log("created an XYAccessor for:" + implicitly[ClassTag[T]])
    new XYAccessor[T] {
      val ax = implicitly[AX[T]]
      val ay = implicitly[AY[T]]

      def size: Int = 2

      def get(tgt: T, result: Array[Float]) {
        result(0) = ax.get(tgt)
        result(1) = ay.get(tgt)
      }

      def set(tgt: T, values: Array[Float]) {
        ax.set(tgt, values(0))
        ay.set(tgt, values(1))
      }
    }
  }
  def pimpedAAccessor[T:AA:ClassTag] = {
    log("created AAccessor for:"+implicitly[ClassTag[T]])
    new AAccessor[T] {
      val aa = implicitly[AA[T]]
      def size: Int = 1
      def get(tgt: T, result: Array[Float]) = result(0) = aa.get(tgt)
      def set(tgt: T, values: Array[Float]): Unit = aa.set(tgt,values(0))
    }
  }
  def pimpedRAccessor[T:AR:ClassTag] = {
    log("created RAccessor for:"+implicitly[ClassTag[T]])
    new AAccessor[T] {
      val aa = implicitly[AR[T]]
      def size: Int = 1
      def get(tgt: T, result: Array[Float]) = result(0) = aa.get(tgt)
      def set(tgt: T, values: Array[Float]): Unit = aa.set(tgt,values(0))
    }
  }

  def pimpedGAccessor[T:AG:ClassTag] = {
    log("created GAccessor for:"+implicitly[ClassTag[T]])
    new AAccessor[T] {
      val aa = implicitly[AG[T]]
      def size: Int = 1
      def get(tgt: T, result: Array[Float]) = result(0) = aa.get(tgt)
      def set(tgt: T, values: Array[Float]): Unit = aa.set(tgt,values(0))
    }
  }

  def pimpedBAccessor[T:AB:ClassTag] = {
    log("created BAccessor for:"+implicitly[ClassTag[T]])
    new AAccessor[T] {
      val aa = implicitly[AB[T]]
      def size: Int = 1
      def get(tgt: T, result: Array[Float]) = result(0) = aa.get(tgt)
      def set(tgt: T, values: Array[Float]): Unit = aa.set(tgt,values(0))
    }
  }
  def pimpedRGBAAccessor[T:AR:AG:AB:AA:ClassTag] = {
    log("created RGBAccessor for:"+implicitly[ClassTag[T]])
    new AAccessor[T] {
      val aa = implicitly[AA[T]]
      val ar = implicitly[AR[T]]
      val ag = implicitly[AG[T]]
      val ab = implicitly[AB[T]]
      def size: Int = 4
      def get(tgt: T, result: Array[Float]) = {
        result(0) = ar.get(tgt)
        result(1) = ag.get(tgt)
        result(2) = ab.get(tgt)
        result(3) = aa.get(tgt)
      }
      def set(tgt: T, values: Array[Float]): Unit = {
        ar.set(tgt,values(0))
        ag.set(tgt,values(1))
        ab.set(tgt,values(2))
        aa.set(tgt,values(3))
      }
    }
  }
  def pimpedRGBAccessor[T:AR:AG:AB:ClassTag] = {
    log("created RGBAccessor for:"+implicitly[ClassTag[T]])
    new AAccessor[T] {
      val ar = implicitly[AR[T]]
      val ag = implicitly[AG[T]]
      val ab = implicitly[AB[T]]
      def size: Int = 3
      def get(tgt: T, result: Array[Float]) = {
        result(0) = ar.get(tgt)
        result(1) = ag.get(tgt)
        result(2) = ab.get(tgt)
      }
      def set(tgt: T, values: Array[Float]): Unit = {
        ar.set(tgt,values(0))
        ag.set(tgt,values(1))
        ab.set(tgt,values(2))
      }
    }
  }



  def X[T: XAccessor] =  implicitly[XAccessor[T]]
  def Y[T: YAccessor] = implicitly[YAccessor[T]]
  def XY[T: XYAccessor] = implicitly[XYAccessor[T]]
  def XYZ[T:XYZAccessor] = implicitly[XYZAccessor[T]]
  def A[T:AAccessor] = implicitly[AAccessor[T]]
  def R[T:RAccessor] = implicitly[RAccessor[T]]
  def G[T:GAccessor] = implicitly[GAccessor[T]]
  def B[T:BAccessor] = implicitly[BAccessor[T]]
  def RGBA[T:RGBAAccessor] = implicitly[RGBAAccessor[T]]
  def RGB[T:RGBAccessor] = implicitly[RGBAccessor[T]]
  implicit object actorAX extends AX[Actor] {
    def get(tgt: Actor): Float = tgt.getX

    def set(tgt: Actor, x: Float): Unit = tgt.setX(x)
  }

  implicit object actorAY extends AY[Actor] {
    def get(tgt: Actor): Float = tgt.getY

    def set(tgt: Actor, y: Float): Unit = tgt.setY(y)
  }

  implicit object spriteAX extends AX[Sprite] {
    def get(tgt: Sprite): Float = tgt.getX

    def set(tgt: Sprite, x: Float): Unit = tgt.setX(x)
  }

  implicit object spriteAY extends AY[Sprite] {
    def get(tgt: Sprite): Float = tgt.getY

    def set(tgt: Sprite, y: Float): Unit = tgt.setY(y)
  }

  implicit object colorA extends AA[Color]{
    def get(tgt: Color): Float = tgt.a

    def set(tgt: Color, v: Float): Unit = tgt.a = v
  }
  implicit object colorR extends AR[Color]{
    def get(tgt: Color): Float = tgt.r

    def set(tgt: Color, v: Float): Unit = tgt.r = v
  }
  implicit object colorG extends  AG[Color]{
    def get(tgt: Color): Float = tgt.g

    def set(tgt: Color, v: Float): Unit = tgt.g = v
  }
  implicit object colorB extends AB[Color]{
    def get(tgt: Color): Float = tgt.b
    def set(tgt: Color, v: Float): Unit = tgt.b = v
  }

  implicit object spriteRGBA extends RGBAAccessor[Sprite]{
    def size: Int = 4

    def get(tgt: Sprite, result: Array[Float]): Unit = {
      val c = tgt.getColor
      result(0) = c.r
      result(1) = c.g
      result(2) = c.b
      result(3) = c.a
    }

    def set(tgt: Sprite, values: Array[Float]){
      tgt.setColor(values(0),values(1),values(2),values(3))
    }
  }


  implicit lazy val actorXAccessor: XAccessor[Actor] = pimpedXAccessor
  implicit lazy val actorYAccessor: YAccessor[Actor] = pimpedYAccessor
  implicit lazy val actorXYAccessor: XYAccessor[Actor] = pimpedXYAccessor
  implicit lazy val spriteXAccessor: XAccessor[Sprite] = pimpedXAccessor
  implicit lazy val spriteYAccessor: YAccessor[Sprite] = pimpedYAccessor
  implicit lazy val spriteXYAccessor: XYAccessor[Sprite] = pimpedXYAccessor
  implicit lazy val colorAAccessor = pimpedAAccessor[Color]
  implicit lazy val colorGAccessor = pimpedGAccessor[Color]
  implicit lazy val colorBAccessor = pimpedBAccessor[Color]
  implicit lazy val colorRAccessor = pimpedRAccessor[Color]
  implicit lazy val colorRGBAccessor = pimpedRGBAccessor[Color]
  implicit lazy val colorRGBAAccessor = pimpedRGBAAccessor[Color]
}

