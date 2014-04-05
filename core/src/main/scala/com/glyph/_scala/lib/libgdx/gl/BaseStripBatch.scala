package com.glyph._scala.lib.libgdx.gl

import com.badlogic.gdx.graphics.{GL20, VertexAttributes, Mesh}
import com.glyph._scala.lib.util.Logging
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import java.util
import com.badlogic.gdx.math.Matrix4
import com.glyph._scala.lib.util.reactive.{Varying, Reactor}
import com.glyph._scala.lib.libgdx.GdxUtil

object TypedBatch {

  implicit object UVTrailIsBatchedTrail extends BatchedTrail[UVTrail] {
    override def attributes: VertexAttributes = UVTrail.ATTRIBUTES

    override def createShader: Varying[Option[ShaderProgram]] = ShaderHandler("shader/rotate2.vert", "shader/default.frag").shader

    override def vertices(tgt: UVTrail): Array[Float] = tgt.meshVertices

    override def verticesLength(tgt: UVTrail): Int = tgt.count
  }

}

trait BatchedTrail[T] {
  def attributes: VertexAttributes

  def createShader: Varying[Option[ShaderProgram]]

  def vertices(tgt: T): Array[Float]

  def verticesLength(tgt: T): Int
}

class TypedBatch[T: BatchedTrail](size: Int) extends Reactor with Logging {
  val evidence = implicitly[BatchedTrail[T]]
  val batch = new BaseStripBatch(size, evidence.attributes)
  val combined = new Matrix4()
  val varyingShader = evidence.createShader
  var started = false
  var currentShader: Option[ShaderProgram] = None
  var failed = false
  reactSome(varyingShader) {
    sp => GdxUtil.post {
      //trying to avoid concurrent switching of the shader while rendering
      failed = false
      currentShader.foreach(_.dispose())
      currentShader = Some(sp)
    }
  }

  def begin() {
    if (currentShader.isDefined && !failed) {
      assert(!started)
      try {
        started = true
        val shader = currentShader.get
        shader.begin()
        batch.begin()
        shader.setUniformMatrix("u_projTrans", combined)
        shader.setUniformi("u_texture", 0)
      }catch{
        case e:Throwable =>errE("error at the beginning of this batch")(e) ;failed = true
      }
    }
  }

  def draw(tgt: T) {
    if (currentShader.isDefined && !failed) {
      try{
        batch.draw(currentShader.get, evidence.vertices(tgt), evidence.verticesLength(tgt))
      } catch{
        case e:Throwable => errE("error while drawing target")(e);failed = true
      }
    }else{
      log("failed to draw with shader")
    }
  }

  def end() {
    if (currentShader.isDefined && !failed) {
      assert(started)
      try{
      val shader = currentShader.get
      batch.end(shader)
      shader.end()
      started = false
      } catch{
        case e:Throwable => errE("error at the end of this batch")(e);failed = true
      }
    }
  }
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
    //TODO let the strips do this and avoid memory copy
    System.arraycopy(vertexArray, 0, vertices, position, verticesLength * VERTEX_SIZE)
    position += verticesLength * VERTEX_SIZE
    //done!
  }

  def flush(shader: ShaderProgram) {
    mesh.setVertices(vertices, 0, position)
    mesh.render(shader, GL20.GL_TRIANGLE_STRIP)
    position = 0
  }

  def end(shader: ShaderProgram) = {
    assert(isStarted)
    isStarted = false
    flush(shader)
  }
}
