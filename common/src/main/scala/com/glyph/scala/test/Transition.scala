package com.glyph.scala.test

import scala.annotation.tailrec
import com.badlogic.gdx.math.MathUtils

/**
 * @author proboscis
 */
trait Transition[+T] {
  def destinations:Seq[Transition[T]]
}

object Transitions{
  def toGraph[G](root:G)(implicit ev:G => Seq[G]) = {
    def rec(r:G,set:Set[(G,G)]):Set[(G,G)]={
      val dsts = ev(r)
      val edges = dsts.map(r->_)
      if(edges.forall(set.contains)) set else{
        dsts.flatMap(dst => rec(dst,set ++ edges)).toSet
      }
    }
    rec(root,Set())
  }
  import scalax.collection.Graph
  import scalax.collection.GraphPredef._
  def main(args: Array[String]) {
    class G (name:String){
      var destinations:Seq[G] = Seq()
      override def toString: String = name
    }
    val g = 1 to 100 map (i => new G(i.toString))
    g foreach{
      n => n.destinations = 1 to 1 map (_=>MathUtils.random(1,99)) map g
    }
    val graph = toGraph(g(0))(_.destinations)
    println(graph)
  }
}
