package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.font.FontUtil
import scalaz._
import Scalaz._
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite}
import com.glyph.scala.lib.libgdx.actor.{Tasking, SpriteBatchRenderer}
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import com.badlogic.gdx.graphics.Color
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.pool.Pool
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.glyph.scala.lib.util.updatable.task.tween.Tween
import com.glyph.scala.lib.util.updatable.task.{Parallel, Sequence}
import com.badlogic.gdx.math.{Interpolation, MathUtils}

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
  val renderer = new Group with SpriteBatchRenderer with Tasking
  val font = internalFont("font/corbert.ttf", 50)
  //this must be disposed after using...
  //val regions = font |> (fontToRegionMap(_)(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')))
  val regions = font |> fontToLazyRegionMap
  val sprites = 1 to 100 map (_ => random('0', '9').toChar) map regions map {
    region => val s = manual[Sprite]
      s.setRegion(region)
      s.setSize(region.getRegionWidth,region.getRegionHeight)
      s
  }
  val velocities = new ArrayBuffer[Float]

  //init(()=>random(PI/3f ) - PI/6f,()=>random(900f,1000f),velocities,sprites.length)
  //val updater = update(0,-1000,2)(sprites,velocities)_
  //renderer addDrawable sprites
  //root add renderer
  val seq = auto[Sequence]
  sprites foreach {
    s =>
      import Interpolation._
      import MathUtils._
      s.setPosition(0,0)
      s.setColor(0,0,0,0)
      val par = auto[Parallel]
      val move = auto[Tween[Sprite]]
      val color = auto[Tween[Sprite]]
      par.add(move.setTarget(s,XY).to(random(-200,200),random(-200,200)).in(1f).using(exp10Out))
      par.add(color.setTarget(s,RGBA).to(1,1,0,1).using(exp10Out).in(1f))
      //renderer add par
      seq.add(par)
  }
  renderer.add(seq)
  renderer addDrawable sprites
  root.add(renderer)

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