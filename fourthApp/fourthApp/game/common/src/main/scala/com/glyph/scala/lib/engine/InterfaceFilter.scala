package com.glyph.scala.lib.engine

/**
 * @author glyph
 */
class InterfaceFilter(val pkg:EntityPackage,interests:Manifest[_<:Interface]*){
  val filter = pkg.createInterfaceFilter(interests:_*)
  def accept(e:Entity):Boolean={
    var i = filter.nextSetBit(0)
    var continue = i >= 0
    var result = if (continue) true else false
    while(continue){
      if (e.interfaces.elementBits.get(i)){
        i = filter.nextSetBit(i+1)
        continue = i>= 0
      }else{
        continue = false
        result = false
      }
    }
    result
  }
}
