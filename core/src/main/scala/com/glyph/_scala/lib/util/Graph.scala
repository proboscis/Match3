package com.glyph._scala.lib.util
import scala.collection.{mutable=>m}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Screen
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.{BuilderOps, Builder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.builders.Builders
import shapeless.PolyDefns.->

/**
 * @author glyph
 */
trait Graph[G]{
  def destinations(node:G):Traversable[G]
}

object Graph{
  implicit class Edges[G:Graph](root:G) {
    def edges:Traversable[(G,G)] = new Traversable[(G, G)] {
      def foreach[U](f: ((G, G)) => U): Unit =  {
        val evidence = implicitly[Graph[G]]
        val visited = m.HashMap[G, Boolean]() withDefault (_ => false)
        val stack: m.Stack[G] = m.Stack[G](root)
        while (!stack.isEmpty) {
          val node = stack.pop()
          if (!visited(node)) {
            val dests = evidence.destinations(node)
            dests foreach (d => f(node,d))
            visited.put(node, true)
            stack.pushAll(dests)
          }
        }
      }
    }
    def nodes:Traversable[G] = new Traversable[G]{
      def foreach[U](f: (G) => U): Unit = {
        val evidence = implicitly[Graph[G]]
        val visited = m.HashMap[G, Boolean]() withDefault (_ => false)
        val stack: m.Stack[G] = m.Stack[G](root)
        while (!stack.isEmpty) {
          val node = stack.pop()
          if (!visited(node)) {
            val dests = evidence.destinations(node)
            dests foreach f
            visited.put(node, true)
            stack.pushAll(dests)
          }
        }
      }
    }
  }
  def ancestors[G](root: G, set: Set[(G, G)]) = set.collect {
    case (a, b) if b == root => a
  }
  def descendants[G](root: G, set: Set[(G, G)]) = set.collect {
    case (a, b) if a == root => b
  }
  def relatives[G](root:G,set:Set[(G,G)]) = ancestors(root,set)++descendants(root,set)
}
object Mock{
  import Builders._
  import BuilderOps._
  import scalaz._
  import Scalaz._
  def createScreenBuilder = dummyTexture as new ConfiguredScreen{}
  def main(args: Array[String]){
  }
}
