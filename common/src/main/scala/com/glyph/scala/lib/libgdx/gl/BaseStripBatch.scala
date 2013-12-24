package com.glyph.scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.{GL10, VertexAttributes, Mesh}
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import java.util
import com.badlogic.gdx.math.Matrix4
import com.glyph.scala.lib.util.reactive.Reactor

trait DrawableStrip[T, A] {
  def vertices(tgt: T): Array[Float]

  def length(tgt: T): Int
}

trait DrawableAttribute[T <: VertexAttributes]

trait VertexAttributeHolder {
  def attributes: VertexAttributes
}

class TypedStripBatch[A <: VertexAttributeHolder](size: Int, attributes: A, shader: ShaderProgram) extends Reactor {
  val batch = new BaseStripBatch(size, attributes.attributes)
  val combined = new Matrix4
  var started = false

  def begin() {
    assert(!started)
    started = true
    shader.begin()
    batch.begin()
    shader.setUniformMatrix("u_projTrans", combined)
    shader.setUniformi("u_texture", 0)
  }

  def draw[T](tgt: T)(implicit ev: DrawableStrip[T, A]) {
    batch.draw(shader, ev.vertices(tgt), ev.length(tgt))
  }

  def end() {
    batch.end(shader)
    shader.end()
    assert(started)
    started = false
  }
}

object TypedStripBatch {
  def defaultShader = ShaderHandler("shader/rotate2.vert", "shader/default.frag")
}

/**
 * @author proboscis
 */
class BaseStripBatch(size: Int, attributes: VertexAttributes) extends Logging {
  val VERTEX_SIZE = attributes.vertexSize / 4
  log("vertex size:" + VERTEX_SIZE)
  val mesh = new Mesh(false, size * 2, 0, attributes)
  val vertices: Array[Float] = new Array(mesh.getMaxVertices * VERTEX_SIZE)
  var position = 0
  var isStarted = false

  def begin() {
    assert(!isStarted)
    isStarted = true
  }

  /**
   *
   * @param vertexArray an array with its window size = attruvytes.vertexSize/4
  @param verticesLength number of vertices
   */
  def draw(shader: ShaderProgram, vertexArray: Array[Float], verticesLength: Int) {
    // 2 is for using degenerate triangles
    if (position + (verticesLength + 2) * VERTEX_SIZE >= vertices.length) {
      flush(shader) //draw everything and set position to zero
    } // if there are not enough space
    if (position != 0) {
      //first insert the degenerates if this is not the first stripe
      vertices(position) = vertices(position - VERTEX_SIZE) //update the positions only
      vertices(position + 1) = vertices(position - VERTEX_SIZE + 1)
      util.Arrays.fill(vertices, position + 2, position + VERTEX_SIZE, 0)
      position += VERTEX_SIZE
      vertices(position) = vertexArray(0) //again, position only
      vertices(position + 1) = vertexArray(1)
      util.Arrays.fill(vertices, position + 2, position + VERTEX_SIZE, 0)
      position += VERTEX_SIZE
    }
    //now start copying actual values
    System.arraycopy(vertexArray, 0, vertices, position, verticesLength * VERTEX_SIZE)
    position += verticesLength * VERTEX_SIZE
    //done!
  }

  def flush(shader: ShaderProgram) {
    //log("flush!")
    mesh.setVertices(vertices, 0, position)
    mesh.render(shader, GL10.GL_TRIANGLE_STRIP)
    position = 0
  }

  def end(shader: ShaderProgram) = {
    assert(isStarted)
    isStarted = false
    flush(shader)
  }
}
