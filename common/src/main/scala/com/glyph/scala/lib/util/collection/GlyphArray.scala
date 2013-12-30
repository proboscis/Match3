package com.glyph.scala.lib.util.collection
import com.badlogic.gdx.utils.{Array=>GdxArray}
/**
 * @author glyph
 */
class GlyphArray[T] extends GdxArray[T]{
  def remove(filter:T=>Boolean){
    val itr = iterator()
    while(itr.hasNext){
      val next = itr.next()
      if(filter(next))itr.remove()
    }
  }
}
