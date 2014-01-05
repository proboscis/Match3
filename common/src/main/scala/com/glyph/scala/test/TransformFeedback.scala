package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.{ImmediateModeRenderer, FrameBuffer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.{Color, GL10}

/**
 * @author glyph
 */
class TransformFeedback extends ScreenBuilder{
  def requirements: Set[(Class[_], Seq[String])] = Set()

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen{
    val shaderHandler = new ShaderHandler("shader/point.vert","shader/point.frag")
    val renderer = new ImmediateModeRenderer20(1000,false,true,0)
    val frameBuffers = Array(1 to 2 map (_=>new FrameBuffer(Format.RGBA8888,100,100,false)):_*)
    val renderFunction = shaderHandler.applier{
      shader =>{
        renderer.setShader(shader)
        //shader.begin()
        //shader.setUniformMatrix("u_projTrans", stage.getCamera.combined)
        //shader.setUniformi("u_texture", 0)
        val r = renderer
        r.begin(stage.getCamera.combined,GL10.GL_POINTS)
        drawPrim1(r)
        r.end()
        //shader.end()
      }
    }

    override def render(delta: Float): Unit = {
      super.render(delta)
      renderFunction()
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

    }
  }
}
