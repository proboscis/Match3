package com.glyph.scala.lib.math

import com.badlogic.gdx.math.{MathUtils, Vector2}

/**
 * Vector2 in scala
 * @param a x
 * @param b y
 */
class Vec2(a: Float, b: Float) extends Vector2(a, b) {
  //TODO make this a trait
  def this() = this(0, 0)

  def this(v: Vector2) = this(v.x, v.y)

  def -=(t: Vector2): Vec2 = {
    x = x - t.x
    y = y - t.y
    this
  }

  def -=(f: Float): Vec2 = {
    x -= f;
    y -= f;
    this
  }

  def +=(t: Vector2): Vec2 = {
    x = x + t.x
    y = y + t.y
    this
  }

  def +=(f: Float): Vec2 = {
    x += f;
    y += f;
    this
  }

  def *=(t: Vector2): Vec2 = {
    x = x * t.x
    y = y * t.y
    this
  }

  def *=(f: Float): Vec2 = {
    x *= f;
    y *= f;
    this
  }

  def /=(t: Vector2): Vec2 = {
    x = x / t.x
    y = y / t.y
    this
  }

  def /=(f: Float): Vec2 = {
    x /= f;
    y /= f;
    this
  }

  def +(t: Vector2) = new Vec2(x + t.x, y + t.y)

  def -(t: Vector2) = new Vec2(x - t.x, y - t.y)

  def *(t: Vector2) = new Vec2(x * t.x, y * t.y)

  def /(t: Vector2) = new Vec2(x / t.x, y / t.y)

  def +(f: Float) = new Vec2(x + f, y + f)

  def -(f: Float) = new Vec2(x - f, y - f)

  def *(f: Float) = new Vec2(x * f, y * f)

  def /(f: Float) = new Vec2(x / f, y / f)

  override def set(x:Float, y:Float):Vec2={
    this.x = x
    this.y = y
    this
  }
}

object Vec2 {
  /**
   * creates a new normalized vector with random direction
   * @return
   */
  def random = new Vec2(new Vector2(1, 0).rotate(MathUtils.random(360)))
  val tmp = new Vec2
}
