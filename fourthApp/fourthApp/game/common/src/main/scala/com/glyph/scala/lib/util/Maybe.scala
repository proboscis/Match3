package com.glyph.scala.lib.util

/**
 * @author glyph
 */
  class Maybe[T](value:T){
    def isNull:Boolean = {
      if(value == null){
        true
      }else{
        false
      }
    }
    def checkNull:Boolean ={value == null}
    def ?(f : T => Unit){
      if (value != null){
        f(value)
      }
    }
    def call {}
    def get=value
  }

