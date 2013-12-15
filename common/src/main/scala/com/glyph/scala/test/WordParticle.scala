package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.font.FontUtil
import scalaz._
import Scalaz._
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite}
import com.glyph.scala.lib.libgdx.actor.{SpriteActor, SpriteBatchRenderer}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import com.badlogic.gdx.graphics.Color
import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.util.pool.Pool

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
  val renderer = new Group with SpriteBatchRenderer
  val font = internalFont("font/corbert.ttf", 30)
  //this must be disposed after using...
  val regions = font |> (fontToRegionMap(_)(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')))
  val sprites = 1 to 5 map (_ => random('0', '9').toChar) map regions map (new Sprite(_)) map (new SpriteActor(_))
  val velocities = new ArrayBuffer[Float]


  //init(()=>random(PI/3f ) - PI/6f,()=>random(900f,1000f),velocities,sprites.length)
  //val updater = update(0,-1000,2)(sprites,velocities)_
  //renderer addDrawable sprites
  //root add renderer
  sprites foreach {
    a =>
      import Actions._
      import Interpolation._
      a.setPosition(300, 100)
      a.addAction(parallel(moveTo(a.getX, a.getY + 100f, 1f, exp10Out), fadeOut(1f)))
      root.addActor(a)
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