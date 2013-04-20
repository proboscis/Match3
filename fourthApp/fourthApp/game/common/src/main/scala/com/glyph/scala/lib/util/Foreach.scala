package com.glyph.scala.lib.util

import com.glyph.libgdx.util.ArrayStack


/**
 * @author glyph
 */
trait Foreach[T]{
  self:ArrayStack[T] =>
//  def foreach(f: T=> Unit){
//    var i = 0
//    while(i < self.size()){
//      f (self.get(i))
//      i += 1
//    }
//  }
  def foreach(f: T=>Unit){
    var i = 0
    while(i < self.size()){
      f(self.get(i))
      i += 1
    }
  }
}
