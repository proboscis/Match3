package com.glyph

import scala.annotation.StaticAnnotation
import scala.reflect.macros.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation
/**
 * @author glyph
 */
object helloMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._
    val result = {
      annottees.map(_.tree).toList match {
        case clazz@ClassDef(mods,name,tparams,Template(parents,self,body))::Nil => ClassDef(mods,name,tparams,Template(parents,self,body.head::addMethods(c)(name+"")(body.tail)))
        case module@ModuleDef(mods, name, temp@Template(parents, self, body)) :: Nil =>ModuleDef(mods, name, Template(parents, self,body.head::addMethods(c)(name+"")(body.tail)))
        case any => println(any.getClass.getSimpleName);null
      }
    }
    //"annottees=>"::annottees::"result=>"::result::Nil foreach println
    println("result:\n"+result)
    c.Expr[Any](result)
  }
  def addTree(c:Context)(tgtName:String)(body:List[c.Tree]):List[c.Tree] = {
    import c.universe._
    body.map{
      case definition@DefDef(mods,name,template,nested,types,exp) =>{
        val block = Block(
          List(
            Apply(Select(Select(Ident(newTermName("scala")), newTermName("Predef")), newTermName("println")),List(Literal(Constant("("+tgtName+","+name+")")))),
            ValDef(Modifiers(), newTermName("_$started"), TypeTree(),Select(Ident(newTermName("System")), newTermName("nanoTime"))),
            ValDef(Modifiers(), newTermName("_$result"), types,exp),
            ValDef(Modifiers(), newTermName("_$finished"), TypeTree(), Select(Ident(newTermName("System")), newTermName("nanoTime"))),
            Apply(Select(Select(Ident(newTermName("scala")), newTermName("Predef")), newTermName("print")),List(Apply(Select(Literal(Constant("(")), newTermName("$plus")), List(Apply(Select(Ident(newTermName("_$finished")), newTermName("$minus")), List(Ident(newTermName("_$started")))))))),
            Apply(Select(Select(Ident(newTermName("scala")), newTermName("Predef")), newTermName("println")),List(Literal(Constant(")"))))
            ),
            Ident(newTermName("_$result"))
          )        /*
        println("exp:"+exp)
        println("expRaw:"+showRaw(exp))
        val gen = """
          {
            val _$started = System.nanoTime
            val _$result = """+exp+"""
            val _$finished = System.nanoTime
            scala.Predef.println("("""+name+"""):"+(_$finished-_$started))
            _$result
          }"""
        println("raw:"+showRaw(c.parse(gen)))
        println("generated:"+gen)
        */
        val result = DefDef(mods,name,template,nested,types,block)
        "before"::definition::"after"::result::Nil foreach println
        result
      }
      case any => any
    }
  }
  def addMethods(c:Context)(tgtName:String)(body:List[c.Tree]):List[c.Tree] = {
    import c.universe._
    body.map{
      case definition@DefDef(mods,name,template,nested,types,exp) =>{
        val block = Block(
          List(
            ValDef(Modifiers(), newTermName("_$started"), TypeTree(),Select(Ident(newTermName("System")), newTermName("nanoTime"))),
            ValDef(Modifiers(), newTermName("_$result"), types,exp),
            ValDef(Modifiers(), newTermName("_$finished"), TypeTree(), Select(Ident(newTermName("System")), newTermName("nanoTime"))),
            Apply(
              Select(
                Select(
                  Ident(newTermName("scala")), newTermName("Predef")
                  ),
                newTermName("println")
                ),
              List(
                Apply(
                  Select(
                    Literal(
                      Constant("("+tgtName+","+name+")")
                      ),
                    newTermName("$plus")
                    ),
                  List(
                    Apply(
                      Select(
                        Ident(
                          newTermName("_$finished")),
                        newTermName("$minus")
                        ),
                      List(
                        Ident(newTermName("_$started"))
                        )
                      )
                    )
                  )
                )
              )
            ),
            Ident(newTermName("_$result"))
          )        /*
        println("exp:"+exp)
        println("expRaw:"+showRaw(exp))
        val gen = """
          {
            val _$started = System.nanoTime
            val _$result = """+exp+"""
            val _$finished = System.nanoTime
            scala.Predef.println("("""+name+"""):"+(_$finished-_$started))
            _$result
          }"""
        println("raw:"+showRaw(c.parse(gen)))
        println("generated:"+gen)
        */
        val result = DefDef(mods,name,template,nested,types,block)
        "before"::definition::"after"::result::Nil foreach println
        result
      }
      case any => any
    }
  }
}
class hello extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro helloMacro.impl
}

