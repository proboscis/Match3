package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.screen.{GlyphScreen => GScreen}
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.Logging

/**
 * @author glyph
 */
class ImmediateTest extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] = Set()

  def create(assetManager: AssetManager): GScreen = new ConfiguredScreen with Logging{
    val renderer = new ImmediateModeRenderer20(false, true, 0)

    val records = new ArrayBuffer[Float]()
    stage.addListener(new InputListener {

      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = true

      override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
        log("touch dragged")
        records += x
        records += y
        super.touchDragged(event, x, y, pointer)
      }
    })

    override def render(delta: Float) {
      super.render(delta)
      val camera = stage.getCamera
      val r = renderer
      val color = Color.RED
      val x = 0
      val y = 0
      val height = 100
      val width = 100
      r.begin(camera.combined, GL20.GL_TRIANGLE_STRIP)
      drawStripe()
      r.end();
    }
    def drawStripe(){
      val r = renderer
      var i = 0
      val l = records.length
      val color = Color.RED
      val width = 10
      while(i < l){
        val x = records(i)
        val y = records(i + 1)
        val x1 = x - width
        val y1 = y
        val x2 = x + width
        val y2 = y
        r.color(color.r, color.g, color.b, color.a)
        r.vertex(x1, y1, 0)

        r.color(color.r, color.g, color.b, color.a)
        r.vertex(x2, y2, 0)
        i += 2
      }

    }
  }
}
