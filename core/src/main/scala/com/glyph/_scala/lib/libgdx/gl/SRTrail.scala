package com.glyph._scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.{VertexAttributes, VertexAttribute, Color}
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.glutils.ShaderProgram

/**
 * @author glyph
 */
class SRTrail(val max: Int) extends BaseTrail(max) {
  def vertexSize: Int = SRTrail.VERTEX_SIZE

  def setupMesh() {
    val color = Color.WHITE.toFloatBits
    val _records = records
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
      val width = recordLength / 2 - Math.abs(recordLength / 2 - i)
      v(vi) = x
      v(vi + 1) = y
      v(vi + 2) = nx - x
      v(vi + 3) = ny - y
      v(vi + 4) = width
      if (i == 0) {
        v(0) = x
        v(1) = y
        v(2) = nx - x
        v(3) = ny - y //u
        v(4) = width //v
      }
      vi += VERTEX_SIZE
      v(vi) = x
      v(vi + 1) = y
      v(vi + 2) = nx - x
      v(vi + 3) = ny - y
      v(vi + 4) = -width
      if (i == 0) {
        v(VERTEX_SIZE) = x
        v(VERTEX_SIZE + 1) = y
        v(VERTEX_SIZE + 2) = nx - x
        v(VERTEX_SIZE + 3) = ny - y
        v(VERTEX_SIZE + 4) = -width
      }
      vi += VERTEX_SIZE
      i += 1
    }
  }
}

object SRTrail {
  val VERTEX_SIZE = 2 + 2 + 1
  val WIDTH_ATTRIBUTE_ALIAS = "a_width"
  val WIDTH_ATTRIBUTE = new VertexAttribute(Usage.Generic, 1, WIDTH_ATTRIBUTE_ALIAS)
  val NORMAL_ATTRIBUTE = new VertexAttribute(Usage.Normal, 2, ShaderProgram.NORMAL_ATTRIBUTE)
  val POSITION_ATTRIBUTE_2D = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
  val ATTRIBUTES = new VertexAttributes(POSITION_ATTRIBUTE_2D, NORMAL_ATTRIBUTE, WIDTH_ATTRIBUTE)

}