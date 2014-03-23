package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.{AssetDescriptor, AssetManager}
import com.badlogic.gdx.Screen
import com.glyph._scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.{ImmediateModeRenderer, FrameBuffer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Color, GL10}

/**
 * @author glyph
 */
class VaryingScreen extends ScreenBuilder{

  override def requirements: Seq[AssetDescriptor[_]] = Nil

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen{
    val shaderHandler = new ShaderHandler("shader/point.vert","shader/point.frag")
    val renderer = new ImmediateModeRenderer20(1000,false,true,0)
    val frameBuffers = Array(1 to 2 map (_=>new FrameBuffer(Format.RGBA8888,100,100,false)):_*)

    override def render(delta: Float): Unit = {
      super.render(delta)
    }
    def drawPrim1(r:ImmediateModeRenderer){
      val color = Color.WHITE
      val x = 100
      val y = 100
      val width = 100
      val height = 100
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y+height, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x+width, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x+width,y+height,0)
    }
  }
}
