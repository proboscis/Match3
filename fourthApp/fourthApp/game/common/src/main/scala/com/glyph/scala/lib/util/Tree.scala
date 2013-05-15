package com.glyph.scala.lib.util

/**
 * @author glyph
 */
trait Tree extends Traversable[self.type]{
  self=>
  var hasChildren = false
  lazy val children = new LinkedList[self.type]
  def addChild(child:self.type )=children.push(_);hasChildren = true
  def removeChild(child:self.type )=children.remove(_);hasChildren = children.isEmpty

  def foreach[U](f: (self.type) => U) {
    f(self)
    if (hasChildren){
      children.foreach {
        f(_)
      }
    }
  }
}
