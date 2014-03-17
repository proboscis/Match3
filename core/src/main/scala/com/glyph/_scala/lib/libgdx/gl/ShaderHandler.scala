package com.glyph._scala.lib.libgdx.gl

import scala.concurrent.{Future, ExecutionContext}
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

/**
 * automatically reloads the specified shaders when changed.
 * use apply method to use the backing shader.
 * you must specifically call begin() and end() of the shader.
 * @author glyph
 */
class ShaderHandler(vFile: String, fFile: String) extends Reactor {

  import ShaderHandler._

  var failed = false

  val shader = loadShader(vFile, fFile).map {
    _.flatMap {
      case Success(s) => Some(s)
      case Failure(f) => f foreach (_.printStackTrace()); failed = true; None
    }
  }
  var prevShader:Option[ShaderProgram] = None
  reactVar(shader) {
    s => {
      prevShader foreach(_.dispose())
      failed = false
      prevShader = s
    }
  }
  /**
   * calling this method on rendering thread may cause allocations
   * @param block
   */
  def apply(block: ShaderProgram => Unit) {
    if (!failed && shader().isDefined) {
      try {
        block(shader().get)
      } catch {
        case e: Throwable => e.printStackTrace(); failed = true
      }
    }
  }

  /**
   * use this to avoid allocation on every frame.
   * @param f
   * @return
   */
  def applier(f: ShaderProgram => Unit): () => Unit = () => {
    if (!failed && shader().isDefined) {
      try {
        f(shader().get)
      } catch {
        case e: Throwable => e.printStackTrace(); failed = true
      }
    }
  }

  def applier2(f: ShaderProgram => () => Unit): () => Unit = {
    var renderer: () => Unit = null
    () => {
      if (!failed && shader().isDefined) {
        try {
          if (renderer == null) {
            renderer = f(shader().get)
          }
          renderer()
        } catch {
          case e: Throwable => e.printStackTrace(); failed = true
        }
      }
    }
  }
}
object ShaderUtil{
  implicit val context = com.glyph._scala.lib.injection.GLExecutionContext.context

  def load(vShaderFile:String,fShaderFile:String) = {
    (GdxFile(vShaderFile) ~ GdxFile(fShaderFile)).mapFuture{
      case vt~ft => for {
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
  implicit val context = com.glyph._scala.lib.injection.GLExecutionContext.context

  /**
   * creates a varying option vnel ShaderProgram
   * @param vFile
   * @param fFile
   * @return
   */
  def loadShader(vFile: String, fFile: String) = {
    (GdxFile(vFile) ~ GdxFile(fFile)).mapFuture[ValidationNel[Throwable, ShaderProgram]] {
      case v ~ f => Try((v.toVnel |@| f.toVnel)((ve: String, fr: String) => {
        val result = new ShaderProgram(ve, fr)
        if (!result.isCompiled) throw new RuntimeException("shader compilation failed\n" + result.getLog + "\n" + ve+"\n" + fr)
        result
      })).toVnel.flatten
    }.map(_.map(_.toVnel.flatten))
  }

  /**
   * you must call this on glThread
   * @param vertexShaderPath
   * @param fragmentShaderPath
   * @return
   */
  def loadShaderBlocking(vertexShaderPath:String,fragmentShaderPath:String) = {
    (GdxFile(vertexShaderPath)~ GdxFile(fragmentShaderPath)).map{
      case vTry ~ fTry =>
        for{
        vs <- vTry
        fs <- fTry
      } yield new ShaderProgram(vs,fs)
    }
  }

  def apply(vFile: String, fFile: String) = new ShaderHandler(vFile, fFile)
}
