package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.json.{JSON, RVJSON}
import scala.util.{Failure, Success}

/**
 * @author glyph
 */
class MeshTest extends ConfiguredScreen with Logging with Reactor {
  backgroundColor.set(Color.BLACK)
  val mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
  mesh.setVertices(Array[Float](
    -0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
    0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
    0.5f, 0.5f, 0, 1, 1, 1, 1, 1, 0,
    -0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0))
  /*
  val vertices = GdxFile("test/mesh.json") map{_.flatMap{
    str => JSON(str).vertices.toArray[Float]
  }}
  */
  val vertices = RVJSON(GdxFile("test/mesh.json")).vertices.toArray[Float]
  reactVar(vertices){
    case Success(s) => mesh.setVertices(s)
    case Failure(e) => e.printStackTrace()
  }
  mesh.setIndices(Array[Short](0, 1, 2, 2, 3, 0))
  ShaderProgram.COLOR_ATTRIBUTE
  val texture = new Texture(Gdx.files.internal("data/sword.png"))
  val matrix = new Matrix4()
  val shader = ShaderHandler("shader/default.vert", "shader/default.frag")
  val updater = shader.applier{
    s =>
      s.begin()
      s.setUniformMatrix("u_projTrans", matrix)
      s.setUniformi("u_texture", 0)
      mesh.render(s, GL10.GL_TRIANGLES)
      s.end()
  }
  override def render(delta: Float) {
    super.render(delta)
    texture.bind()
    updater()
  }
}

