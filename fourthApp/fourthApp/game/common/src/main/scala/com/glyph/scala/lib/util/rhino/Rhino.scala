package com.glyph.scala.lib.util.rhino

import com.glyph.scala.lib.util.DebugUtil
import org.mozilla.javascript.{ScriptableObject, Context}
import scala.util.control.Exception._
import com.glyph.scala.lib.util.json.{RJSON, JSON}
import com.glyph.scala.lib.util.reactive.Varying

class Rhino {
  val scope = Context.enter().initStandardObjects()
  Context.exit()

  def +=(name: String, value: Any) {
    Context.enter()
    ScriptableObject.putProperty(scope, name, Context.javaToJS(value, scope))
    Context.exit()
  }

  def apply[T: Manifest](script: String): Either[Throwable, T] = {
    var result: Either[Throwable, T] = null
    Rhino.log("load javascript") {
      val context = Context.enter
      context.setOptimizationLevel(-1) //no byte-code generation
      result = allCatch.either(context.evaluateString(scope, script, "", 1, null)).right.flatMap {
        r => allCatch.either(Context.jsToJava(r, implicitly[Manifest[T]].runtimeClass).asInstanceOf[T])
      }
      Context.exit()
    }
    result
  }
}

/**
 * @author glyph
 */
object Rhino {
  val log = DebugUtil.log(println) _
  def evaluate[T: Manifest](script: String, env: Seq[(String, Any)] = Nil): Either[Throwable, T] = {
    val rhino = new Rhino
    env foreach {
      case (n, v) => rhino +=(n, v)
    }
    rhino.apply(script)
  }
  def apply(script:String,env:Map[String,Any]):JSON ={
    val rhino = new Rhino
    env foreach {
      case (n, v) => rhino +=(n, v)
    }
    new JSON(rhino[Object](script),rhino.scope)
  }
  def apply(script:Varying[String],env:Map[String,Any] = Map.empty):RJSON = {
    RJSON(script,env)
  }
}
