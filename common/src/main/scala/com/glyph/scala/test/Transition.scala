package com.glyph.scala.test

import com.badlogic.gdx.math.MathUtils
import scala.collection.mutable
import scalax.collection.edge

/**
 * @author proboscis
 */
trait Transition[+T] {
  def destinations:Seq[Transition[T]]
}

object Transitions{
  def toGraph[G](root:G)(implicit ev:G => Seq[G]) = {
    val visited = mutable.HashMap[G,Boolean]() withDefault (_=>false)
    var edges:Seq[(G,G)] = Seq()
    val stack:collection.mutable.Stack[G] = mutable.Stack[G](root)
    while(!stack.isEmpty){
      val node = stack.pop()
      if(!visited(node)){
        println(node)
        val dsts = ev(node)
        edges ++= dsts map (node->_)
        visited.put(node,true)
        stack.pushAll(dsts)
      }
    }
    edges
    /*
    def rec(r:G,set:Set[(G,G)]):Set[(G,G)]={
      val edges = ev(r).map(r->_).toSet
      val newEdges = edges.filterNot(set.contains)
      val newSet = set ++ newEdges
      set ++ newEdges.flatMap(dst=>rec(dst._2,newSet))
    }
    rec(root,Set())
    */
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
      n => n.destinations = 1 to 3 map (_=>MathUtils.random(1,99)) map g
    }
    val graph = toGraph(g(0))(_.destinations)
    println(graph)
  }
}
