package com.glyph.test

import com.glyph.scala.lib.util.reactive.{Reactor, EventSource}
import com.glyph.scala.lib.util.hfsm.{HReactiveEmpty, ReactiveHState}

/**
 * @author glyph
 */
object StateTest {
  val src1 = EventSource[Unit]()
  val src2 = EventSource[Unit]()
  val state = new HReactiveEmpty

  def main(args: Array[String]) {
    state.addChild(Q1)
    for (i <- 1 to 10) {
      src1.emit(null)
      src2.emit(null)
    }
  }

  import ReactiveHState.-->


  object Q1 extends ReactiveHState with Reactor{
    var counter: Counter = null

    override def onEnter() {
      counter = new Counter(src1, 4)
      addChild(counter)
      reactEvent(src1){
        _=>transit(Q2)
      }
      super.onEnter()
    }


    override def onExit() {
      super.onExit()
      clearReaction()
    }

    val handleEvent: --> = {
      case (src, e) if src == src1 => Q2
    }

    def dependencies = Set(src1)
  }

  object Q2 extends ReactiveHState {
    val handleEvent: --> = {
      case (src, e) if src eq src2 => Q1
    }
    def dependencies = Set(src2)
  }

  class Counter(src: EventSource[_], n: Int) extends ReactiveHState {
    var count = 0
    val finish = EventSource[Unit]()
    val handleEvent: --> = {
      case (s, e) if src eq s => {
        println("count:"+count)
        count += 1
        if (count == n) null else this
      }
    }
    def dependencies = Set(src)
  }

}
