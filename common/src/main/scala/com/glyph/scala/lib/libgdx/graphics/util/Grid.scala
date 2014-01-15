package com.glyph.scala.lib.graphics.util

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class Grid(nx:Int,ny:Int,var size:Float){
  def draw(s:ShapeRenderer){
    val tx = nx/2*size
    val ty = ny/2*size
    s.translate(-tx,-ty,0)
    var x = 0
    while ( x < nx){
      var y = 0
      while( y < ny){
        s.rect(x*size,y*size,size,size)
        y += 1
      }
      x += 1
    }
    s.translate(tx,ty,0)
  }
}
