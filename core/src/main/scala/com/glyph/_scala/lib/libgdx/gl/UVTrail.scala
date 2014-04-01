package com.glyph._scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.{VertexAttributes, VertexAttribute, Color}
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.glutils.ShaderProgram

/**
 * @author glyph
 */
class UVTrail(size: Int) extends BaseTrail(size) {
  def vertexSize: Int = UVTrail.VERTEX_SIZE
  val color = Color.WHITE.cpy()
  def setupMesh() {
    // these assignments are required for optimization
    val _records = records
    val c = color.toFloatBits
    val recordLength = count / 2 //records.length/2
    var i = 0
    val v = meshVertices
    val VERTEX_SIZE = vertexSize
    var vi = VERTEX_SIZE * 2
    while (i < recordLength - 1) {
      //for all vertices
      val ri = i * 2
      val x = _records(ri)
      val y = _records(ri + 1)
      val nx = _records(ri + 2)
      val ny = _records(ri + 3)
      v(vi) = x
      v(vi + 1) = y
      v(vi + 2) = c
      v(vi + 3) = nx - x
      v(vi + 4) = ny - y
      v(vi + 5) = -1
      v(vi + 6) = i / MAX.toFloat
      if (i == 0) {
        v(0) = x
        v(1) = y
        v(2) = c
        v(3) = nx - x
        v(4) = ny - y //u
        v(5) = -1 //v
        v(6) = 0
      }
      vi += VERTEX_SIZE
      v(vi) = x
      v(vi + 1) = y
      v(vi + 2) = c
      v(vi + 3) = nx - x
      v(vi + 4) = ny - y
      v(vi + 5) = 1
      v(vi + 6) = i / MAX.toFloat
      if (i == 0) {
        v(VERTEX_SIZE) = x
        v(VERTEX_SIZE + 1) = y
        v(VERTEX_SIZE + 2) = c
        v(VERTEX_SIZE + 3) = nx - x
        v(VERTEX_SIZE + 4) = ny - y
        v(VERTEX_SIZE + 5) = 1
        v(VERTEX_SIZE + 6) = 0
      }
      vi += VERTEX_SIZE
      i += 1
    }
  }
}

object UVTrail {
  val VERTEX_SIZE = 2 + 1 + 2 + 1 + 1
  val WIDTH_ATTRIBUTE_ALIAS = "a_width"
  val LENGTH_ATTRIBUTE_ALIAS = "a_length"
  val LENGTH_ATTRIBUTE = new VertexAttribute(Usage.Generic, 1, LENGTH_ATTRIBUTE_ALIAS)
  val WIDTH_ATTRIBUTE = new VertexAttribute(Usage.Generic, 1, WIDTH_ATTRIBUTE_ALIAS)
  val NORMAL_ATTRIBUTE = new VertexAttribute(Usage.Normal, 2, ShaderProgram.NORMAL_ATTRIBUTE)
  val POSITION_ATTRIBUTE_2D = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
  val ATTRIBUTE_SEQ = POSITION_ATTRIBUTE_2D :: VertexAttribute.Color() :: NORMAL_ATTRIBUTE :: WIDTH_ATTRIBUTE :: LENGTH_ATTRIBUTE :: Nil
  val ATTRIBUTES = new VertexAttributes(ATTRIBUTE_SEQ: _*)
}
