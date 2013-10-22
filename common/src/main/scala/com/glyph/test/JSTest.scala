package com.glyph.test

import com.glyph.scala.lib.util.DebugUtil
import com.glyph.scala.lib.util.reactive.{Reactor, RFile}
import com.glyph.scala.lib.util.json.{RVJSON, RJSON}

/**
 * @author glyph
 */
object JSTest extends Reactor {
  def main(args: Array[String]) {
    val log = DebugUtil.log(println) _
    val compiler = new RFile("common/src/main/resources/js/coffee-script.js")
    val script = new RFile("common/src/main/resources/js/test.js")
    /*
    reactVar(script) {
      script =>
        log("evaluate") {
          val context = Context.enter
          context.setOptimizationLevel(-1)
          val scope: Scriptable = context.initStandardObjects()
          val bind = Context.javaToJS(println(_: String), scope)
          ScriptableObject.putProperty(scope, "println", bind)
          ScriptableObject.defineClass(scope, classOf[A])
          val result = context.evaluateString(scope, script, "", 1, null)
          println(result)
          //Context.jsToJava(result,classOf[Card]).asInstanceOf[Card]
          type O = NativeObject
          val obj = Context.jsToJava(result, classOf[O]).asInstanceOf[O]
          println(obj.get("stream"))
          Context.exit()
        }
    }
    class A extends ScriptableObject {
      def getClassName: String = "A"
    }
    */
    val rjson = RVJSON(script)
    reactVar(rjson) {
      json => for {
        first <- json.flatMap{_.first.as[String]}
        last <- json.flatMap{_.last.as[String]}
      } {
        println(first + "," + last)
      }
    }
  }
}
