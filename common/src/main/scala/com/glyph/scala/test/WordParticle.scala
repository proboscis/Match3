package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.font.FontUtil
import scalaz._
import Scalaz._
import com.badlogic.gdx.graphics.g2d.{TextureRegion, BitmapFont, Sprite}
import com.glyph.scala.lib.libgdx.actor.{Tasking, SpriteBatchRenderer}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import com.badlogic.gdx.graphics.Color
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.pool.Pool
import com.glyph.scala.game.Glyphs
import Glyphs._
import aurelienribon.tweenengine._
import aurelienribon.tweenengine.Tween
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.math.MathUtils

/**
 * @author glyph
 */
class WordParticle extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] = Set()

  def create(assetManager: AssetManager): Screen = new WordParticleScreen
}

class WordParticleScreen extends ConfiguredScreen {

  import FontUtil._
  import com.badlogic.gdx.math.MathUtils._

  backgroundColor = Color.BLACK
  implicit val renderer = new Group with SpriteBatchRenderer with Tasking
  val font = internalFont("font/corbert.ttf", 50)
  //this must be disposed after using...
  //val regions = font |> (fontToRegionMap(_)(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')))
  val regions = font |> fontToLazyRegionMap
  //val sprites = WordParticle.StringToSprites(font)("0123456789")
  //init(()=>random(PI/3f ) - PI/6f,()=>random(900f,1000f),velocities,sprites.length)
  //val updater = update(0,-1000,2)(sprites,velocities)_
  //renderer addDrawable sprites
  //root add renderer
  implicit val manager = new TweenManager
  Tween.registerAccessor(classOf[Sprite], SpriteAccessor)
  Tween.setCombinedAttributesLimit(4)

  //WordParticle.popSprites(sprites)(-200, 0, () =>100, 0.5f).repeat(100, 0).start(manager)

  WordParticle.popStrings(font)("hello")

  root.add(renderer)


  override def render(delta: Float) {
    manager.update(delta)
    super.render(delta)
  }

  override def dispose() {
    super.dispose()
    font.dispose()
  }
}

class MessagePopper(font: BitmapFont)(implicit spritePool: Pool[Sprite], actorPool: Pool[Actor]) {

  import FontUtil._

  val regions = font |> (fontToRegionMap(_)(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')))
  /*
  def createMessage(msg:String):Seq[Actor] = {
    msg map regions map{
      region =>
    }
  }*/
}

object WordParticle extends Logging {

  import Pool._

  def StringToSprites(font: BitmapFont)(string: String)(implicit pool: Pool[Sprite]): Seq[Sprite] = {
    var px = 0f
    val texture = font.getRegion.getTexture
    val data = font.getData
    string map {
      c => val glyph = data.getGlyph(c)
        val sprite = manual[Sprite]
        sprite.setTexture(texture)
        import glyph._
        sprite.asInstanceOf[TextureRegion].setRegion(srcX:Int,srcY:Int,width:Int,height:Int)
        sprite.setSize(width,height)
        sprite.setX(px + xoffset)
        px += width //+ xadvance
        sprite
    }
  }

  def popSprites(sprites: Seq[Sprite])(x: Float, y: Float, amount: () => Float, duration: Float): Timeline = {
    val line = Timeline.createSequence()
    import SpriteAccessor._
    import TweenEquations._
    sprites foreach{
      s => line.push(Tween.set(s, XY).target(s.getX+x, s.getY+y))
    }
    line.beginParallel()
    sprites.zipWithIndex.foreach {
      case (s, i) =>
        log(s.getWidth)
        line.push(
          Timeline.createSequence().delay(i*0.1f).
          push(Tween.to(s, XY, duration).target(s.getX+x, s.getY+y + amount()).ease(easeOutExpo))
        )
    }
    line.end()
    line
  }

  def popStrings(font:BitmapFont)(string:String)(implicit pool:Pool[Sprite],renderer:SpriteBatchRenderer,manager:TweenManager){
    val sprites = StringToSprites(font)(string)
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

  def popStrings(implicit pool:Pool[Sprite]) = StringToSprites(_:BitmapFont)(_:String)(pool) |> (sp=> popSprites(sp)_->sp)

  def popTween(seq: Seq[(Any, Float, Float)], typ: Int)(x: Float, y: Float, amount: Float): Timeline = {
    val line = Timeline.createParallel()
    seq.zipWithIndex.foreach {
      case ((s, px, py), i) => line.push(Tween.set(s, typ).target(px, py))
    }
    line
  }
}