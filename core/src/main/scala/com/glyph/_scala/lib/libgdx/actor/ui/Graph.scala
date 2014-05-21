package com.glyph._scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Batch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class Graph(values: IndexedSeq[Float], renderer: ShapeRenderer,min:Float,max:Float) extends Group {

  import ShapeType._

  val lineBuf = Array.fill(4)(0f)

  override def draw(batch: Batch, parentAlpha: Float) {
    val size = values.size
    if (size > 1) {
      batch.end()
      renderer begin Line
      renderer setProjectionMatrix batch.getProjectionMatrix
      renderer.getTransformMatrix.setTranslation(getX,getY,0)
      renderer setColor Color.GREEN
      var i = 1
      val diff = max - min
      val dx = getWidth / size
      val h = getHeight
      def height(index: Int) = (values(index) - min) / diff * h
      lineBuf(2) = 0f
      lineBuf(3) = height(0)
      while (i < size) {
        lineBuf(0) = lineBuf(2)
        lineBuf(1) = lineBuf(3)
        lineBuf(2) = i * dx
        lineBuf(3) = height(i)
        renderer.polyline(lineBuf)
        i += 1
      }
      renderer end()
      batch.begin()
    }

  }
}

class RingArray(max: Int) extends IndexedSeq[Float] {
  def length: Int = Math.min(_length, max)

  private var _length = 0
  private var index = 0
  private val values = Array.fill(max)(0f)

  def apply(idx: Int): Float = values((index - _length + idx + max) % max)

  def +=(v: Float) {
    if (_length < max) {
      _length += 1
    }
    values(index) = v
    index += 1
    if (index >= max) index = 0
  }
}
