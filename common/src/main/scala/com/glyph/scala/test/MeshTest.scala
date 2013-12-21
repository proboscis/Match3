package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.{Logging, reactive}
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import scalaz._
import Scalaz._
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.game.Glyphs
import Glyphs._

/**
 * @author glyph
 */
class MeshTest extends ConfiguredScreen with Logging with Reactor {
  backgroundColor.set(Color.BLACK)
  val vert = GdxFile("shader/default.vert")
  val frag = GdxFile("shader/default.frag")

  import reactive._


  implicit object context extends ExecutionContext {
    def reportFailure(t: Throwable): Unit = t.printStackTrace()

    def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)
  }

  val shader = vert ~ frag mapFuture {
    case v ~ f =>
      for {
        vs <- v
        fs <- f
      } yield {
        val result = new ShaderProgram(vs, fs)
        if (result.getLog.contains("error")) throw new RuntimeException("shader compilation failed\n" + result.getLog)
        result
      }
  }

  /*
  val shader = vert ~ frag map {
    case v ~ f => Future(Try((v |@| f)((ve: String, fr: String) => {
      val result = new ShaderProgram(ve, fr)
      if (result.getLog.contains("error")) throw new RuntimeException("shader compilation failed\n" + result.getLog)
      result
    }: ShaderProgram)).toVnel)
  }*/
  val mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
  mesh.setVertices(Array[Float](
    -0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
    0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
    0.5f, 0.5f, 0, 1, 1, 1, 1, 1, 0,
    -0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0))
  mesh.setIndices(Array[Short](0, 1, 2, 2, 3, 0));
  val texture = new Texture(Gdx.files.internal("data/sword.png"))
  val matrix = new Matrix4()

  val shaderFrag = Var(false)
  reactVar(shader)(_ => shaderFrag() = false)

  override def render(delta: Float) {
    super.render(delta)
    texture.bind()
    if (!shaderFrag()) {
      for (st <- shader()) {
        st.flatten match {
          case util.Success(s) => {
            try {
              s.begin()
              s.setUniformMatrix("u_worldView", matrix)
              s.setUniformi("u_texture", 0)
              mesh.render(s, GL10.GL_TRIANGLES)
              s.end()
            } catch {
              case e => e.printStackTrace(); shaderFrag() = true
            }
          }
          case util.Failure(e) => e.printStackTrace(); shaderFrag() = true
        }
      }
    }
  }
}

object RShader {

  import reactive._

  implicit object context extends ExecutionContext {
    def reportFailure(t: Throwable): Unit = t.printStackTrace()

    def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)
  }

  def apply(vFile: String, fFile: String) = {

    GdxFile(vFile) ~ GdxFile(fFile) map {
      case f ~ v => Future(Try((v.toVnel |@| f.toVnel)((ve: String, fr: String) => {
        val result = new ShaderProgram(ve, fr)
        if (result.getLog.contains("error")) throw new RuntimeException("shader compilation failed\n" + result.getLog)
        result
      })).toVnel.flatMap(identity))
    }
  }
}
