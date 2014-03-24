package com.glyph._scala.lib.libgdx.gl

import scala.concurrent.ExecutionContext
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import scala.util.Try
import com.glyph._scala.game.Glyphs
import Glyphs._
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.reactive.{Varying, Reactor}
import com.glyph._scala.lib.libgdx.GLFuture
import com.glyph._scala.lib.util.Logging

/**
 * automatically reloads the specified shaders when changed.
 * use apply method to use the backing shader.
 * you must specifically call begin() and end() of the shader.
 * @author glyph
 */
class ShaderHandler(vFile: String, fFile: String) extends Logging {

  import ShaderUtil._

  val shader = load(vFile, fFile).map {
    case Some(util.Success(sp)) => Some(sp)
    case Some(util.Failure(f))=>errE("shader compilation failed")(f);None
    case None => err("shader is not yet compiled"); None
  }

  def applier2(f: ShaderProgram => () => Unit) = new (() => Unit) with Reactor {
    var renderer: () => Unit = () => {}
    var prevShader: Option[ShaderProgram] = None
    var failed = false
    reactVar(shader) {
      shaderOpt =>
        prevShader foreach (_.dispose())
        failed = false
        if (shaderOpt.isDefined) {
          renderer = f(shaderOpt.get)
        }
        prevShader = shaderOpt
    }

    override def apply(): Unit = {
      if (!failed && shader().isDefined) {
        try {
          renderer()
        } catch {
          case e: Throwable => errE("error while rendering with shader")(e); failed = true
        }
      }
    }
  }
}

object ShaderUtil {
  implicit val context = com.glyph._scala.lib.injection.GLExecutionContext

  def load(vShaderFile: String, fShaderFile: String) = {
    (GdxFile(vShaderFile) ~ GdxFile(fShaderFile)).mapFuture {
      case vt ~ ft => for {
        vs <- vt
        fs <- ft
      } yield Try {
          val result = new ShaderProgram(vs, fs)
          if (!result.isCompiled) throw new RuntimeException("shader compilation failed\n" + result.getLog + "\n" + vs + "\n" + fs)
          result
        }
    }.map(_.map(_.flatten.flatten))
  }
}

object ShaderHandler {
  def apply(vFile: String, fFile: String) = new ShaderHandler(vFile, fFile)
}
