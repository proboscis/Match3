package com.glyph.scala.lib.util.hfsm

/**
 * @author glyph
 */
trait HState {
  private var _parent: Option[HState] = None
  private var _children: List[HState] = Nil
  def children = _children
  def parent = _parent
  private var nextState:Option[HState] = None

  val handleEvent: PartialFunction[Any, Boolean]
  def propagate(event: Any): Boolean = {
    (_children exists {
      _.propagate(event)
    }) || (
      handleEvent.isDefinedAt(event) && handleEvent(event))
  }

  //TODO this request should be posted
  def changeTo(next:HState) {
    next match {
      case n if n != this => _parent.foreach {
        p => p.removeChild(this)
        p.addChild(n)
      }
      case n if n == this => //nothing happens
    }
  }
  def exit(){
    for (p <- _parent)p.removeChild(this)
  }

  def addChild(child: HState) {
    _children = child :: _children
    child._parent = Some(this)
    child.onEnter()
  }

  def removeChild(child: HState) {
    println("HState=>removeChild:"+child)
    if (!_children.contains(child)) throw new RuntimeException("there is no such child:" + child)
    _children = _children.filter {
      c => c ne child
    }
    child.onExit()
  }

  def onEnter() {
    println("enter"+this.getClass.getSimpleName)
  }

  def onExit() {
    println("exit"+this.getClass.getSimpleName)
    _children foreach removeChild
  }
}

class HEmpty extends HState {
  val handleEvent: PartialFunction[Any, Boolean] = PartialFunction.empty
}

object HState{
  def empty = new HEmpty
}