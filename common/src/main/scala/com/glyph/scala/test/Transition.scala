package com.glyph.scala.test

/**
 * @author proboscis
 */
trait Transition[+T] {
  def destinations:Seq[Transition[T]]
}

object Transition{
  def toGraph[G](root:G)(implicit ev:G => Seq[G]) = {
    def rec(r:G,set:Set[(G,G)]):Set[(G,G)]={
      val dsts = ev(r)
      val edges = dsts.map(r->_)
      if(edges.forall(set.contains)) set else{
        set ++ dsts.flatMap(dst => rec(dst,set))
      }
    }
    rec(root,Set())
  }
  import scalax.collection.Graph
  import scalax.collection.GraphPredef._
  def main(args: Array[String]) {
    class G {
      var destinations:Seq[G] = Seq()
    }
    val a = new G
    val b = new G
    val c = new G
    a.destinations = Seq(b,c)
    b.destinations = Seq(a,c)
    c.destinations = Seq(a,b)
    val graph = toGraph(a)(_.destinations)
    println(graph)
  }
}
