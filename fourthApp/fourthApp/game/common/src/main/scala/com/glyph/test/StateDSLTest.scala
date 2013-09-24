package com.glyph.test

import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import scala.collection.immutable.Stack


trait HSM{
  var stack = Stack.empty[State]
  def state(block: State=>Unit):State = {
    stack +:= new State
    stack.top
  }

  /**
   * testing
   * @param block
   */
  def enter(block: =>Unit)
  def exit(block: =>Unit)
  def react[T](v:Var[T])(f:(T)=>Unit){

  }
  def idle = new State{

  }
  class State extends Reactor
}

/**
 * @author glyph
 */
object StateDSLTest {
  def main(args: Array[String]) {
    var stack = Stack(0)
    stack +:=1
    stack +:=2
    while (!stack.isEmpty){
      println(stack.top)
      stack = stack.pop
    }
  }
}
