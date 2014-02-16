package com.glyph._scala.lib.util.rhino

import org.mozilla.javascript.{ScriptableObject, Context}
import scala.util.control.Exception._
import com.glyph._scala.lib.util.json.{RJSON, JSON}
import com.glyph._scala.lib.util.reactive.Varying
import com.glyph._scala.lib.util.Logging
import scala.util.Try

class Rhino {
  val scope = Context.enter().initStandardObjects()
  Context.exit()

  def +=(name: String, value: Any) {
    Context.enter()
    ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope))
    Context.exit()
  }

  def apply[T: Manifest](script: String):Try[T]= {
    // Rhino.log("load javascript") {
    val context = Context.enter
    context.setOptimizationLevel(-1) //no byte-code generation
    val result = Try(context.evaluateString(scope, script, "", 1, null)).flatMap {
      r => Try(Context.jsToJava(r, implicitly[Manifest[T]].runtimeClass).asInstanceOf[T])
    }
    Context.exit()
    // }
    result
  }
}

/**
 * @author glyph
 */
object Rhino extends Logging {
  def evaluate[T: Manifest](script: String, env: Seq[(String, Any)] = Nil):Try[T] = {
    val rhino = new Rhino
    env foreach {
      case (n, v) => rhino +=(n, v)
    }
    rhino.apply(script)
  }

  def apply(script: String, env: Map[String, Any]): JSON = {
    val rhino = new Rhino
    env foreach {
      case (n, v) => rhino +=(n, v)
    }
    new JSON(rhino[Object](script), rhino.scope)
  }
}
