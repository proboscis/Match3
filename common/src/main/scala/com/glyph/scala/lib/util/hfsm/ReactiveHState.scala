package com.glyph.scala.lib.util.hfsm

import com.glyph.scala.lib.util.reactive.{EventSource, Reactor}
import com.glyph.scala.lib.util.hfsm.ReactiveHState._
import scala.Some

/**
 * Hierarchical State graph
 * @author glyph
 */
trait ReactiveHState extends Reactor{
  //TODO if you split foreach and filter, it's fine

  import ReactiveHState.-->

  val handleEvent: -->
  private val reactor = new Reactor {}
  private var _parent: Option[ReactiveHState] = None
  private var _children: List[ReactiveHState] = Nil

  def parent = _parent

  def children = _children

  def addChild(child: ReactiveHState) {
    _children = (child :: _children)
    child._parent = Some(this)
    child.onEnter()
  }

  def removeChild(child: ReactiveHState) {
    if (!_children.contains(child)) throw new RuntimeException("there is no such child:" + child)
    _children = _children.filter {
      c => c ne child
    }
    child.onExit()
  }

  def definedDependencies: Set[EventSource[_]] = {
    _parent.map {
      _.definedDependencies
    }.getOrElse(Set.empty) union dependencies
  }

  private def propagate(src: EventSource[_], event: Any): Boolean = {
    (_children exists {
      _.propagate(src, event)
    }) ||
      (handleEvent.isDefinedAt(src, event) && {
        transit(handleEvent(src, event))
        true
      })
  }

  def dependencies: Set[EventSource[_]] //Setは集合を意味する(重複が消去される)


  /**
   * you can call this method to force this state to transit into specified state.</br>
   * passing a null will result in removing this state from the parent.
   * @param next
   */
  def transit(next: ReactiveHState) {
    println("transit:"+next)
    if (next != null) {
      if (next ne this) {
        //if the state is changed...
        _parent.foreach {
          p =>
            p.removeChild(this)
            p.addChild(next)
        }
      }
    }else{
      _parent.foreach{
        _.removeChild(this)
      }
    }
  }

  def onEnter() {
    println("enter:" + getClass.getSimpleName)
    (dependencies diff _parent.map {
      _.definedDependencies
    }.getOrElse(Set.empty)) foreach {
      //差分だけ登録
      src =>
        reactEvent(src) {
          e => propagate(src, e)
        }
    }
  }

  def onExit() {
    println("exit:" + getClass.getSimpleName)
    clearReaction()
    _children foreach (removeChild)
  }
}

object ReactiveHState {
  type --> = PartialFunction[(EventSource[_], Any), ReactiveHState]
}

class HReactiveEmpty extends ReactiveHState {
  val handleEvent: --> = PartialFunction.empty
  def dependencies = Set.empty
}
