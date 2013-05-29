package com.glyph.test


/**
 * @author glyph
 */
object Test {
  def main(args: Array[String]) {
      val i = 1

  }
  class Cond(b:Boolean)(f: =>Unit){
    def when(b_ :Boolean)(f_ : =>Unit):Cond={
      new Cond(b_)(f_)
    }
    def or(){}
  }
  implicit def Unit2Cond(u:Unit):Cond={
    new Cond(true)()
  }
}
