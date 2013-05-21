package com.glyph.scala.lib.graphics.util

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Camera, Color}
import com.glyph.scala.lib.util.drawable.RequireCamera

/**
 * @author glyph
 */
class World (val unit:Float) extends RequireCamera{
  val renderer = new ShapeRenderer()
  val grid = new Grid(40,40,unit)

  def draw(camera:Camera){
    renderer.setProjectionMatrix(camera.combined)
    renderer.begin(ShapeRenderer.ShapeType.Line)
    renderer.identity()
    renderer.rotate(1,0,0,90)
    renderer.setColor(Color.DARK_GRAY)
    grid.draw(renderer)
    renderer.identity()
    renderer.setColor(Color.RED)
    renderer.line(0,0,0,unit,0,0)//x axis
    renderer.setColor(Color.GREEN)
    renderer.line(0,0,0,0,unit,0)//y axis
    renderer.setColor(Color.BLUE)
    renderer.line(0,0,0,0,0,unit)//z axis

    renderer.end()
  }
}
