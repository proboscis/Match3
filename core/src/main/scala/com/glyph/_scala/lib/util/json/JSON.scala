package com.glyph._scala.lib.util.json

import scala.language.dynamics
import org.mozilla.javascript.{NativeArray, ScriptableObject, Context, NativeObject}
import com.glyph._scala.lib.util.reactive.{Reactor, Varying}
import com.glyph._scala.lib.util.rhino.Rhino
import reflect.ClassTag
import scalaz._
import Scalaz._
import RJSON.VNET
import RJSON.TJSON
import java.util.Map.Entry
import scala.util.Try
import com.glyph._scala.lib.libgdx.reactive.GdxFile

/**
 * @author glyph
 */
class JSON(o: Try[Object], scope: ScriptableObject) extends Dynamic {

  def apply(key: String): JSON = {
    new JSON(o.flatMap {
      t => Try(t.asInstanceOf[NativeObject].get(key))
    }, scope)
  }

  def apply(index: Int): JSON = {
    new JSON(o.flatMap {
      t => Try(t.asInstanceOf[NativeObject].get(index))
    }, scope)
  }

  def asMapTry: Try[Map[String, JSON]] = o.flatMap {
    t => Try {
      t.asInstanceOf[NativeObject].entrySet().toArray(Array[Entry[Object, Object]]()).foldLeft(Map[String, JSON]()) {
        case (map, set) =>
          map + (set.getKey.asInstanceOf[String] -> new JSON(util.Success(set.getValue), scope))
      }
    }
  }

  def toArrayTry[T: ClassTag]: Try[Array[T]] = o.flatMap {
    t => Try {
      val classT = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
      val ids = t.asInstanceOf[NativeArray].toArray
      val l = ids.length
      var i = 0
      val ary = new Array[T](ids.length)
      classT match {
        case f if f == classOf[Float] =>
          while (i < l) {
            ary(i) = ids(i).asInstanceOf[Double].toFloat.asInstanceOf[T]
            i += 1
          }
        case f if f == classOf[Int] =>
          while (i < l) {
            ary(i) = ids(i).asInstanceOf[Double].toInt.asInstanceOf[T]
            i += 1
          }
        case f if f == classOf[Short] =>
          while (i < l) {
            ary(i) = ids(i).asInstanceOf[Double].toShort.asInstanceOf[T]
            i += 1
          }
        case _ =>
          while (i < l) {
            ary(i) = classT.cast(ids(i))
            i += 1
          }
      }
      ary
    }
  }

  def asFunction: JSFunction = o match {
    case util.Success(s) => new JSFunction(s.asInstanceOf[org.mozilla.javascript.Function])
    case util.Failure(f) => throw f
  }

  def asFunctionTry: Try[JSFunction] = o.flatMap {
    t => Try {
      new JSFunction(t.asInstanceOf[org.mozilla.javascript.Function])
    }
  }


  def asOpt[T: ClassTag]: Option[T] = as[T] match {
    case util.Success(s) => Some(s)
    case util.Failure(f) => f.printStackTrace(); None
  }

  def as[T: ClassTag]: Try[T] = o.flatMap {
    t => Try {
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
  }

  def asVnel[T: ClassTag]: VNET[T] = as[T] match {
    case util.Success(s) => s.successNel
    case util.Failure(f) => f.failNel
  }

  def selectDynamic(name: String): JSON = apply(name)

  def applyDynamic(name: String)(arg: Any): JSON = arg match {
    case s: String => apply(name)(s)
    case n: Int => apply(name)(n)
    case u: Unit => apply(name)
  }

  class JSFunction(func: org.mozilla.javascript.Function) extends (Seq[Any] => JSON) {
    def apply(params: Seq[Any] = Nil): JSON = {
      val result = new JSON(Try {
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
    new JSON(rhino.eval[Object](script), rhino.scope)
  }

  def applyTry(script: Try[String], env: Map[String, Any] = Map()): JSON = {
    val rhino = new Rhino
    env foreach {
      case (k, v) => rhino +=(k, v)
    }
    new JSON(script.flatMap(rhino.eval[Object]), rhino.scope)
  }
}


class RVJSON(o: Varying[TJSON]) extends Varying[Option[JSON]] with Dynamic with Reactor {
  var variable: Option[JSON] = null

  implicit def vnel2opt[T](vnel: VNET[T]): Option[T] = vnel.fold(nel => {
    nel.foreach(_.printStackTrace());
    none
  }, _.some)

  reactVar(o) {
    vnel => variable = vnel.toOption; notifyObservers(variable)
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

  def toArray[T: ClassTag]: Varying[Try[Array[T]]] = o.map {
    _.flatMap {
      _.toArrayTry
    }
  }

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
  def as[T: ClassTag]: Varying[Option[T]] = o.map(_.toOption.flatMap(_.asOpt[T]))

  def asVnel[T: ClassTag]: Varying[VNET[T]] = o.map {
    case util.Success(s) => s.asVnel
    case util.Failure(f) => f.failNel
  }

  def asFunction: Varying[VNET[JSON#JSFunction]] = o.map(_.flatMap(_.asFunctionTry) match {
    case util.Success(s) => s.successNel
    case util.Failure(f) => f.failNel
  })
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

  def as[T: ClassTag]: Varying[Try[T]] = o.map {
    _.as[T]
  }

  def asFunction: Varying[Try[JSON#JSFunction]] = o.map {
    _.asFunctionTry
  }
}

object GdxJSON {
  def apply(fileName: String, environment: Map[String, Any] = Map()) = new RJSON(GdxFile(fileName).map(JSON.applyTry(_, environment)))
}

object RJSON {
  type VNET[T] = ValidationNel[Throwable, T]
  type TJSON = Try[JSON]

  def apply(script: Varying[String], env: Map[String, Any] = Map.empty): RJSON = {
    //println("new RJSON:"+script())
    new RJSON(script map {
      str => JSON(str, env)
    })
  }
}

object RVJSON {
  def apply(script: Varying[Try[String]], env: Map[String, Any] = Map.empty): RVJSON = {
    new RVJSON(script.map(_.map(JSON(_, env))))
  }
}