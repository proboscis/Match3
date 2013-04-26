package com.glyph.scala.lib.util.laz /**
 * @author glyph
 */
class Lazy[T<:LazyValue[V],V](value:T){
  var mInitialized = false
  def initIfNeeded(t:V):T={
    if (!mInitialized){
      mInitialized = value.initialize(t)
    }
    value
  }
}
