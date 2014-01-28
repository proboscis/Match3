package com.glyph.scala.test

import com.badlogic.gdx.math.MathUtils
import scala.collection.mutable
import scalax.collection.edge
import scala.collection.mutable.ListBuffer

/**
 * @author proboscis
 */
trait Transition[+T] {
  def destinations:Seq[Transition[T]]
}

object Transitions{
  def toGraph[G](root:G)(implicit ev:G => Seq[G]) = {
    val visited = mutable.HashMap[G,Boolean]() withDefault (_=>false)
    val edges:ListBuffer[(G,G)] = ListBuffer.empty
    val stack:collection.mutable.Stack[G] = mutable.Stack[G](root)
    while(!stack.isEmpty){
      val node = stack.pop()
      if(!visited(node)){
        val dests = ev(node)
        edges ++= dests map (node->_)
        visited.put(node,true)
        stack.pushAll(dests)
      }
    }
    edges
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
    println(graph.size)
  }
}