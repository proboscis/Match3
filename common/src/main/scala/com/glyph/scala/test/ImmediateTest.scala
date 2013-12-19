package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.screen.{GlyphScreen => GScreen}
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
import com.badlogic.gdx.graphics.{Texture, Color, GL20}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.math.{Vector2, Matrix3, MathUtils}

/**
 * @author glyph
 */
class ImmediateTest extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] = Set(classOf[Texture]->Seq("data/sword.png"))
  def create(assetManager: AssetManager): GScreen = new ConfiguredScreen with Logging{
    val renderer = new ImmediateModeRenderer20(false, true, 8)
    val texture = assetManager.get[Texture]("data/sword.png")
    backgroundColor = Color.BLUE
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
      r.begin(camera.combined, GL20.GL_TRIANGLE_STRIP)
      texture.bind()
      val color = Color.WHITE
      r.color(color.r,color.g,color.b,color.a)
      r.texCoord(0,0)
      r.vertex(0,0,0)

      r.color(color.r,color.g,color.b,color.a)
      r.texCoord(1,0)
      r.vertex(100,0,0)

      r.color(color.r,color.g,color.b,color.a)
      r.texCoord(0,1)
      r.vertex(0,100,0)

      r.color(color.r,color.g,color.b,color.a)
      r.texCoord(1,1)
      r.vertex(100,100,0)

      //drawStripe()
      r.end()
    }
    val tmp = new Vector2
    val m = new Matrix3()
    def drawStripe(){
      val r = renderer
      var i = 0
      val l = records.length
      val color = Color.RED
      val width = 30
      while(i < l){
        val px = records(if(i > 2) i-2 else i)
        val py = records(if(i > 2) i-1 else i+1)
        val x = records(i)
        val y = records(i + 1)
        val deg = MathUtils.atan2(x-px,y-py)*MathUtils.radDeg
        tmp.set(-width,0)
        tmp.rotate(-deg)
        tmp.add(x,y)
        val x1 = tmp.x
        val y1 = tmp.y
        tmp.set(width,0)
        tmp.rotate(-deg)
        tmp.add(x,y)
        val x2 = tmp.x
        val y2 = tmp.y
        r.color(color.r, color.g, color.b, color.a)
        r.vertex(x1, y1, 0)
        r.texCoord(0,i*0.1f)

        r.color(color.r, color.g, color.b, color.a)
        r.vertex(x2, y2, 0)
        r.texCoord(1,i*0.1f)
        i += 2
      }

    }
  }
}
