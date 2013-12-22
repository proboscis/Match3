package com.glyph.scala.lib.libgdx

import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Sprite, BitmapFont}
import com.glyph.scala.lib.util.pool.Pool
import com.badlogic.gdx.graphics.Color
import aurelienribon.tweenengine._
import com.glyph.scala.lib.libgdx.actor.SpriteBatchRenderer
import com.glyph.scala.game.Glyphs._
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
object WordParticle extends Logging {
  def StringToSprites(font: BitmapFont)(string: String)(scale:Float)(implicit pool: Pool[Sprite]): Seq[Sprite] = {
    var px = 0f
    val texture = font.getRegion.getTexture
    val data = font.getData
    string map {
      c => val glyph = data.getGlyph(c)
        val sprite = manual[Sprite]
        sprite.setTexture(texture)
        import glyph._
        sprite.asInstanceOf[TextureRegion].setRegion(srcX:Int,srcY:Int,width:Int,height:Int)
        sprite.setSize(width*scale,height*scale)
        sprite.setColor(Color.WHITE)
        sprite.setX((px + xoffset)*scale)
        px += width //+ xadvance
        sprite
    }
  }

  import SpriteAccessor._
  import TweenEquations._
  def popSprites(sprites: Seq[Sprite])(x: Float, y: Float, amount: () => Float, duration: Float): Timeline = {
    val line = Timeline.createSequence()
    sprites foreach{
      s => line.push(Tween.set(s, XY).target(s.getX+x, s.getY+y))
    }
    line.beginParallel()
    sprites.zipWithIndex.foreach {
      case (s, i) =>
        log(s.getWidth)
        line.push(
          Timeline.createSequence().delay(i*0.1f).
          push(Tween.to(s, XY, duration/2f).target(s.getX+x, s.getY+y + amount()).ease(easeOutExpo)).
          push(Tween.to(s,RGBA,duration/2f).target(0,0,0,0).ease(easeInExpo))
        )
    }
    line.end()
    line
  }

  def popStrings(font:BitmapFont)(string:String)(implicit pool:Pool[Sprite],renderer:SpriteBatchRenderer,manager:TweenManager){
    val sprites = StringToSprites(font)(string)(0.5f)
    start(sprites,popSprites(sprites)(0,0,()=>100f,1f))
  }
  def start(sprites:Seq[Sprite],timeline:Timeline)(implicit renderer:SpriteBatchRenderer,manager:TweenManager){
    timeline.setCallback(new TweenCallback{
      def onEvent(`type`: Int, source: BaseTween[_]){
        renderer.removeDrawable(sprites)
        sprites foreach(_.free)
      }
    }).setCallbackTriggers(TweenCallback.COMPLETE).start(manager)
    renderer.addDrawable(sprites)
  }

  def popStrings(implicit pool:Pool[Sprite]) = StringToSprites(_:BitmapFont)(_:String)(_:Float)(pool) |> (sp=> popSprites(sp)_->sp)

  def popTween(seq: Seq[(Any, Float, Float)], typ: Int)(x: Float, y: Float, amount: Float): Timeline = {
    val line = Timeline.createParallel()
    seq.zipWithIndex.foreach {
      case ((s, px, py), i) => line.push(Tween.set(s, typ).target(px, py))
    }
    line
  }
}
