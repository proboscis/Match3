package com.glyph.scala.lib.util

/**
 * @author glyph
 */
trait Chainable{
  self =>
  def chain(f: self.type => Unit):self.type ={
    f(self)
    self
  }
}
