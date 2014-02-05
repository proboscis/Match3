package com.glyph._scala.lib.libgdx.actor

import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.reactive.{Reactor, Var}
import com.glyph._scala.lib.util.reactive
import com.badlogic.gdx.math.Rectangle

/**
 * @author glyph
 */
trait ReactiveSize extends Actor{
  val rX = Var(getX,"ReactiveSize:rX")
  val rY = Var(getY,"ReactiveSize:rY")
  val rWidth = Var(getWidth,"ReactiveSize:rWidth")
  val rHeight = Var(getHeight,"ReactiveSize:rHeight")
  import reactive._
  val rRect = rX~rY~rWidth~rHeight map {case rx~ry~w~h =>new Rectangle(rx,ry,w,h)}

  override def setWidth(w: Float) {
    super.setWidth(w)
    rWidth() = w
  }

  override def setHeight(h: Float) {
    super.setHeight(h)
    rHeight() = h
  }

  override def setX(x: Float) {
    super.setX(x)
    this.rX() = x
  }

  override def setY(y: Float) {
    super.setY(y)
    this.rY() = y
  }

  override def setSize(width: Float, height: Float) {
    super.setSize(width, height)
    this.rWidth() = width
    this.rHeight() = height
  }

  override def size(size: Float) {
    super.size(size)
    this.rWidth() = getWidth
    this.rHeight() = getHeight
  }

  override def size(width: Float, height: Float) {
    super.size(width, height)
    this.rWidth() = getWidth
    this.rHeight() = getHeight
  }

  override def setPosition(x: Float, y: Float) {
    super.setPosition(x, y)
    this.rX() = x
    this.rY() = y

  }
}
