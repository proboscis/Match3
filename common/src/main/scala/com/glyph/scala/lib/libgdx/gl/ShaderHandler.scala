package com.glyph.scala.lib.libgdx.gl

import scala.concurrent.ExecutionContext
import com.badlogic.gdx.Gdx
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import scala.util.Try
import com.glyph.scala.game.Glyphs
import Glyphs._
import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.reactive.Reactor

/**
 * automatically reloads the specified shaders when changed.
 * use apply method to use the backing shader.
 * you must specifically call begin() and end() of the shader.
 * @author glyph
 */
class ShaderHandler(vFile: String, fFile: String) extends Reactor {

  import ShaderHandler._

  val shader = loadShader(vFile, fFile)
  reactVar(shader){
    _ => failed = false
  }
  var failed = false

  /**
   * calling this method on rendering thread may cause allocations
   * @param block
   */
  def apply(block: ShaderProgram => Unit) {
    if (!failed && shader().isDefined) {
      shader().get match {
        case Success(s) => {
          try {
            block(s)
          } catch {
            case e: Throwable => e.printStackTrace(); failed = true
          }
        }
        case Failure(e) => e foreach (_.printStackTrace()); failed = true
      }
    }
  }

  /**
   * use this to avoid allocation on every frame.
   * @param f
   * @return
   */
  def applier(f: ShaderProgram => Unit):()=>Unit = ()=>{
    if (!failed && shader().isDefined) {
      shader().get match {
        case Success(s) => {
          try {
            f(s)
          } catch {
            case e: Throwable => e.printStackTrace(); failed = true
          }
        }
        case Failure(e) => e foreach (_.printStackTrace()); failed = true
      }
    }
  }
}

object ShaderHandler {

  /**
   * use this when you have something to be done on the opengl thread
   */
  implicit object context extends ExecutionContext {
    def reportFailure(t: Throwable): Unit = t.printStackTrace()

    def execute(runnable: Runnable): Unit = Gdx.app.postRunnable(runnable)
  }

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
        if (result.getLog.contains("error")) throw new RuntimeException("shader compilation failed\n" + result.getLog)
        result
      })).toVnel.flatten
    }.map(_.map(_.toVnel.flatten))
  }

  def apply(vFile: String, fFile: String) = new ShaderHandler(vFile, fFile)

}