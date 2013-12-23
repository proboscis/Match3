package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.{Input, Gdx}
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.math.{MathUtils, Matrix3, Vector2, Matrix4}
import com.badlogic.gdx.graphics.glutils.{ShapeRenderer, ShaderProgram}
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.glyph.scala.lib.util.reactive.Reactor
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import java.util
import com.glyph.scala.lib.util.{ArrayOps, Logging}

/**
 * @author glyph
 */
class TrailTest extends ConfiguredScreen with Reactor {
  val mesh = new Mesh(true, 4, 0, VertexAttribute.Position(), VertexAttribute.Color, VertexAttribute.TexCoords(0));
  val meshFile = RVJSON(GdxFile("test/stripMesh.json"))
  reactSuccess(meshFile.vertices.toArray[Float])(mesh.setVertices)
  reactSuccess(meshFile.vertices.toArray[Float]) {
    ary => println(ary.size, ary.toSeq)
  }
  //reactSuccess(meshFile.indices.toArray[Short])(mesh.setIndices)
  val texture = new Texture(Gdx.files.internal("data/sword.png"))
  ShaderProgram.pedantic = false
  val shader = ShaderHandler("shader/default.vert", "shader/effect2.frag")
  var time = 0f
  val trail = new Trail(50)
  val matrix = stage.getCamera.combined
  val updater = shader.applier {
    s =>
      s.begin()
      s.setUniformMatrix("u_projTrans", matrix)
      //s.setUniformi("u_texture", 0)
      s.setUniformf("time", time)
      s.setUniformf("resolution", 1080, 1920)
      s.setUniformf("mouse", 0, 0)
      trail.mesh.render(s, GL10.GL_TRIANGLE_STRIP)
      s.end()
  }

  override def render(delta: Float) {
    super.render(delta)
    texture.bind()
    updater()
    WireRenderer.setColor(Color.RED)
    //WireRenderer.drawWire(mesh,6, GL10.GL_TRIANGLE_STRIP, matrix)
    WireRenderer.setColor(Color.BLUE)
    WireRenderer.drawLines(trail.records, trail.count)
    WireRenderer.setColor(Color.ORANGE)
    WireRenderer.drawWire(trail.mesh, 5, GL10.GL_TRIANGLE_STRIP, matrix)
    time += delta
  }

  stage.addListener(new InputListener {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      super.touchDown(event, x, y, pointer, button)
      true
    }

    override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int): Unit = {
      super.touchDragged(event, x, y, pointer)
      trail.add(x, y)
    }

    override def keyDown(event: InputEvent, keycode: Int): Boolean = keycode match {
      case Input.Keys.R => trail.reset(); true
      case Input.Keys.W => false
      case _ => false
    }
  })
}

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
   * @param vertexArray an array containing pos,color,uv (window == 5)
   * @param verticesLength number of vertices
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

class StripBatch(size: Int) extends BaseStripBatch(size, Trail.ATTRIBUTES)

class Trail(val max: Int) extends BaseTrail(max) {

  import Trail._

  val mesh = new Mesh(false, MAX * 2, 0, POSITION_ATTRIBUTE_2D, VertexAttribute.Color(), VertexAttribute.TexCoords(0))


  def vertexSize: Int = Trail.VERTEX_SIZE

  def addVertices() {
    val v = meshVertices
    val color = Color.WHITE.toFloatBits
    val recordLength = count / 2
    val r = records
    if (recordLength > 1) {
      val px = r(count - 4)
      val py = r(count - 3)
      val x = r(count - 2)
      val y = r(count - 1)
      val width = 5
      val angle = -MathUtils.atan2(x - px, y - py) * MathUtils.radDeg
      t2.set(x - px, y - py)
      m1.setToRotation(angle)
      //t1.set(0, -width).mul(m1).add(nx, ny)
      t1.set(-width, 0).mul(m1).add(x, y)
      var vi = (recordLength - 1) * 2 * VERTEX_SIZE
      v(vi) = t1.x
      v(vi + 1) = t1.y
      v(vi + 2) = color
      v(vi + 3) = 0 //u
      v(vi + 4) = 0 //v
      if (recordLength == 2) {
        v(0) = t1.x - t2.x
        v(1) = t1.y - t2.y
        v(2) = color
        v(3) = 0 //u
        v(4) = 0 //v
      }
      vi += VERTEX_SIZE
      t1.set(width, 0).mul(m1).add(x, y)
      v(vi) = t1.x
      v(vi + 1) = t1.y
      v(vi + 2) = color
      v(vi + 3) = 0 //u
      v(vi + 4) = 0 //v
      if (recordLength == 2) {
        v(5) = t1.x - t2.x
        v(6) = t1.y - t2.y
        v(7) = color
        v(8) = 0 //u
        v(9) = 0 //v
      }
      vi += VERTEX_SIZE
      //mesh.setVertices(v,0,vi)
    }
  }

  //this is a bit heavy ops
  //these rotations are the heavy ops!
  def setupMesh() {
    val color = Color.WHITE.toFloatBits
    val recordLength = count / 2 //records.length/2
    var i = 0
    val v = meshVertices
    var vi = 10
    while (i < recordLength - 1) {
      //for all vertices
      val ri = i * 2
      val x = records(ri)
      val y = records(ri + 1)
      val nx = records(ri + 2)
      val ny = records(ri + 3)
      val width = recordLength / 2 - Math.abs(recordLength / 2 - i)
      //val width = 10
      val angle = -MathUtils.atan2(nx - x, ny - y) * MathUtils.radDeg
      //val angle = t2.set(nx - x, ny - y).angle()
      m1.setToRotation(angle)
      //t1.set(0, -width).mul(m1).add(nx, ny)
      t1.set(-width, 0).mul(m1).add(nx, ny)
      v(vi) = t1.x
      v(vi + 1) = t1.y
      v(vi + 2) = color
      v(vi + 3) = 0 //u
      v(vi + 4) = 0 //v
      if (i == 0) {
        v(0) = t1.x - t2.x
        v(1) = t1.y - t2.y
        v(2) = color
        v(3) = 0 //u
        v(4) = 0 //v
      }
      //t1.set(0, width).mul(m1).add(nx, ny)
      t1.set(width, 0).mul(m1).add(nx, ny)
      vi += 5
      v(vi) = t1.x
      v(vi + 1) = t1.y
      v(vi + 2) = color
      v(vi + 3) = 0 //u
      v(vi + 4) = 0 //v
      if (i == 0) {
        v(5) = t1.x - t2.x
        v(6) = t1.y - t2.y
        v(7) = color
        v(8) = 0 //u
        v(9) = 0 //v
      }
      vi += 5
      i += 1
    }
    //println(vi)
    mesh.setVertices(v, 0, vi)
  }

  override def reset() {
    super.reset()
    mesh.setVertices(EMPTY)
  }
}

object Trail {
  val EMPTY = Array.empty[Float]
  val VERTEX_SIZE = 2 + 1 + 2
  val POSITION_ATTRIBUTE_2D = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
  val ATTRIBUTES = new VertexAttributes(Trail.POSITION_ATTRIBUTE_2D, VertexAttribute.Color(), VertexAttribute.TexCoords(0))
  val t1 = new Vector2
  val t2 = new Vector2
  val t3 = new Vector2
  val m1 = new Matrix3
}

object WireRenderer {
  val shapeRenderer = new ShapeRenderer(30000)
  val verticesTmp = new Array[Float](10000)
  val vertices = new Array[Float](10000)

  def setColor(color: Color) {
    shapeRenderer.setColor(color)
  }

  def drawWire(mesh: Mesh, stride: Int, primitive: Int, combined: Matrix4) {
    shapeRenderer.setProjectionMatrix(combined)
    primitive match {
      case GL10.GL_TRIANGLE_STRIP => drawStrip(mesh, stride)
      case _ =>
    }
  }

  def drawStrip(mesh: Mesh, stride: Int) {
    if (mesh.getNumVertices > 2) {
      mesh.getVertices(0, verticesTmp)
      import ArrayOps._
      verticesTmp.copyStride(mesh.getNumVertices * stride, vertices, stride, 2)
      // println(vertices.take(20).toSeq)
      //  println(verticesTmp.take(20).toSeq)

      shapeRenderer.begin(ShapeType.Line)
      shapeRenderer.polyline(vertices, 0, mesh.getNumVertices * 2)
      shapeRenderer.end()
    }
  }

  def drawLines(vb: IndexedSeq[Float], length: Int) {
    val sr = shapeRenderer
    if (length > 2) {
      sr.begin(ShapeType.Line)
      vb.copyToArray(vertices)
      sr.polyline(vertices, 0, length)
      sr.end()
    }
  }
}

