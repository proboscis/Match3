package com.glyph.test

/**
 * @author glyph
 */
object SECDMachine {
  def main(args: Array[String]) {
    //new SECD(L(V("a"),V("a")))
  }
  trait Expr
  case class L(left:V,right:Expr) extends Expr
  case class V(v:String) extends Expr
/*
  class SECD(expr:Expr){
    println(expr)
    case class Dump(s:List,e:List,c:List,d:Dump)
    var S = List()
    var E = List()
    var C = List()
    var D = Dump(null,null,null,null)
    evaluate(expr)
    def evaluate(e:Expr){
      (S,E,C,D) match{
        case (s,e,c,d) if c.isEmpty =>{
          //D = Dump(s.head:D.s,D.e,D.c,D.d)
        }
        case (s,e,c,d) if !c.isEmpty =>{

        }
        case _ =>
      }
      println(e)
    }
  }
  */
}
