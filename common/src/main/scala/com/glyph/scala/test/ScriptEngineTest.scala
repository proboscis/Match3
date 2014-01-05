package com.glyph.scala.test

import java.io.{PrintWriter, File}
import com.glyph.scala.lib.util.reactive._
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import com.googlecode.scalascriptengine.{SourcePath, Config, ScalaScriptEngine}
import com.glyph.scala.lib.util.Logging
import scala.reflect.io.{Path, Directory}
import com.googlecode.scalascriptengine.Config
import scala.util.Failure
import scala.Some
import scala.util.Success
import com.googlecode.scalascriptengine.SourcePath

/**
 * @author glyph
 */
object ScriptEngineTest extends Reactor with Logging {
  def main(args: Array[String]) {
    val classChecker = new ClassScripter("./common/src/main/scala", "./.changed", "./.changed/classes")
    val cls = classChecker.getClass[Test, Hello]
    reactSome(cls) {
      c => {
        log("class changed", c)
        println(c.newInstance().result)
      }
    }
  }
}

trait Test {
  def result: String
}