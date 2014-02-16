package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph._scala.lib.libgdx.font.FontUtil
import scalaz._
import Scalaz._
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite}
import com.glyph._scala.lib.libgdx.actor.{Tasking, SpriteBatchRenderer}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import com.badlogic.gdx.graphics.Color
import com.glyph._scala.lib.util.pool.Pool
import com.glyph._scala.game.Glyphs
import Glyphs._
import aurelienribon.tweenengine._
import com.glyph._scala.lib.libgdx.WordParticle

/**
 * @author glyph
 */
class WordParticle extends ScreenBuilder {
  def requirements: Set[(Class[_], Seq[String])] = Set()

  def create(implicit assetManager: AssetManager): Screen = new WordParticleScreen

}

class WordParticleScreen extends ConfiguredScreen {

  import FontUtil._

  implicit val spritePool = Pool[Sprite](1000)
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

