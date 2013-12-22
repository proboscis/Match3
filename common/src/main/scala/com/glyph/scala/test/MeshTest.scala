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
import scala.collection.mutable.ArrayBuffer

/**
 * @author glyph
 */
class MeshTest extends ConfiguredScreen with Logging with Reactor {
  backgroundColor.set(Color.BLACK)
  val mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
  val meshFile = RVJSON(GdxFile("test/mesh.json"))
  reactSuccess(meshFile.vertices.toArray[Float])(mesh.setVertices)
  reactSuccess(meshFile.indices.toArray[Short])(mesh.setIndices)
  val texture = new Texture(Gdx.files.internal("data/sword.png"))
  val matrix = new Matrix4()
  ShaderProgram.pedantic = false
  val shader = ShaderHandler("shader/default.vert", "shader/effect2.frag")
  var time = 0f
  val updater = shader.applier{
    s =>
      s.begin()
      s.setUniformMatrix("u_projTrans", matrix)
      //s.setUniformi("u_texture", 0)
      s.setUniformf("time",time)
      s.setUniformf("resolution",1080,1920)
      s.setUniformf("mouse",0,0)
      mesh.render(s, GL10.GL_TRIANGLES)
      s.end()
  }
  override def render(delta: Float) {
    super.render(delta)
    texture.bind()
    updater()
    time += delta
  }
}
