package com.glyph.scala.lib.util.json

import scala.language.dynamics
import org.mozilla.javascript.{ScriptableObject, Context, NativeObject}
import com.glyph.scala.lib.util.reactive.{Reactor, Varying}
import com.glyph.scala.lib.util.rhino.Rhino
import util.control.Exception._
import reflect.ClassTag
import scalaz._
import Scalaz._
import RJSON.VNET
import RJSON.VNETJ
import org.mozilla.javascript.annotations.JSFunction

/**
 * @author glyph
 */
class JSON(o: Either[Throwable, Object], scope: ScriptableObject) extends Dynamic {
  def apply(key: String): JSON = {
    new JSON(o.right.flatMap {
      t => allCatch either
        t.asInstanceOf[NativeObject].get(key)
    }, scope)
  }

  def apply(index: Int): JSON = {
    new JSON(o.right.flatMap {
      t => allCatch either
        t.asInstanceOf[NativeObject].get(index)
    }, scope)
  }


  def asFunction: VNET[JSFunction] = o.right.flatMap {
    t => allCatch either {
      new JSFunction(t.asInstanceOf[org.mozilla.javascript.Function])
    }
  }.fold(_.failNel, _.success)


  def as[T: ClassTag]: Option[T] = o.right.flatMap {
    t => allCatch either {
      val clazz = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
      //println("cast %s to %s".format(t.getClass, clazz))
      val result: T = t match {
        case t: java.lang.Double => clazz match {
          case d if d == classOf[Double] => t.asInstanceOf[T]
          case i if i == classOf[Int] => t.toInt.asInstanceOf[T]
          case f if f == classOf[Float] => t.toFloat.asInstanceOf[T]
        }
        case f: org.mozilla.javascript.Function => clazz match {
          case ft if ft.isAssignableFrom(classOf[() => _]) => new JSFunction(f).asInstanceOf[T]
        }
        case b: java.lang.Boolean => b.booleanValue().asInstanceOf[T]
        case _ => clazz.cast(t)
      }
      //println("success")
      result
    }
  }.fold[Option[T]]({e => e.printStackTrace();none},t => some(t))

  def selectDynamic(name: String): JSON = apply(name)

  def applyDynamic(name: String)(arg: Any): JSON = arg match {
    case s: String => apply(name)(s)
    case n: Int => apply(name)(n)
    case u: Unit => apply(name)
  }

  class JSFunction(func: org.mozilla.javascript.Function) extends (Seq[Any] => JSON) {
    def apply(params: Seq[Any] = Nil): JSON = {
      val result = new JSON(allCatch either {
        func.call(Context.enter(), scope, scope, params.toArray.asInstanceOf[Array[AnyRef]])
      }, scope)
      Context.exit()
      result
    }

    def apply(a: Any, params: Any*): JSON = apply(a :: params.toList)
  }

}

object JSON {
  def apply(script: String, env: Map[String, Any] = Map.empty): JSON = {
    val rhino = new Rhino
    env foreach {
      case (k, v) => rhino +=(k, v)
    }
    new JSON(rhino[Object](script), rhino.scope)
  }
}


class RVJSON(o: Varying[VNETJ]) extends Varying[Option[JSON]] with Dynamic with Reactor {
  var variable: Option[JSON] = null
  implicit def vnel2opt[T](vnel:VNET[T]):Option[T] =vnel.fold(nel=>{nel.foreach(_.printStackTrace());none},_.some)
  reactVar(o) {
    vnel => variable = vnel; notifyObservers(variable)
  }

  def current = variable

  def apply(key: String): RVJSON = new RVJSON(o map {
    _.map {
      _.apply(key)
    }
  })

  def apply(idx: Int): RVJSON = new RVJSON(o map {
    _.map {
      _(idx)
    }
  })

  def selectDynamic(name: String): RVJSON = apply(name)

  def applyDynamic(name: String)(arg: Any): RVJSON = arg match {
    case s: String => apply(name)(s)
    case n: Int => apply(name)(n)
    case u: Unit => apply(name)
  }
/*
  def as[T: ClassTag]: Varying[VNET[T]] = o.map {
    _.flatMap(_.as[T])
  }*/
  def as[T:ClassTag]:Varying[Option[T]] = o.map{_.fold({
  nel => nel foreach(_.printStackTrace())
  none
},_.as[T])}

  def asFunction: Varying[VNET[JSON#JSFunction]] = o.map(_.flatMap(_.asFunction))
}

class RJSON(o: Varying[JSON]) extends Varying[JSON] with Dynamic with Reactor {
  var variable: JSON = null
  reactVar(o) {
    json => variable = json; notifyObservers(variable)
  }

  def current: JSON = variable

  def apply(key: String): RJSON = {
    new RJSON(o map {
      _.apply(key)
    })
  }

  def apply(idx: Int): RJSON = {
    new RJSON(o map {
      _.apply(idx)
    })
  }

  def selectDynamic(name: String): RJSON = apply(name)

  def applyDynamic(name: String)(arg: Any): RJSON = arg match {
    case s: String => apply(name)(s)
    case n: Int => apply(name)(n)
    case u: Unit => apply(name)
  }

  def as[T: ClassTag]: Varying[Option[T]] = o.map {
    _.as[T]
  }
  def asFunction: Varying[VNET[JSON#JSFunction]] = o.map {
    _.asFunction
  }

}

object RJSON {
  type VNET[T] = ValidationNel[Throwable, T]
  type VNETJ = VNET[JSON]

  def apply(script: Varying[String], env: Map[String, Any] = Map.empty): RJSON = {
    //println("new RJSON:"+script())
    new RJSON(script map {
      str => JSON(str, env)
    })
  }
}
object RVJSON {
  def apply(script:Varying[VNET[String]],env: Map[String, Any] = Map.empty):RVJSON = {
    new RVJSON(script.map(_.map(JSON(_,env))))
  }
}