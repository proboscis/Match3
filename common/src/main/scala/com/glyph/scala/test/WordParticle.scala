package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx.font.FontUtil
import scalaz._
import Scalaz._
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.lib.libgdx.actor.SpriteBatchRenderer
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.graphics.Color

/**
 * @author glyph
 */
class WordParticle extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Array[WordParticle#FileName])] = Set()

  def create(assetManager: AssetManager): Screen = new WordParticleScreen
}

class WordParticleScreen extends ConfiguredScreen {

  import FontUtil._
  import com.glyph.scala.lib.libgdx.actor.SBDrawableGdx._
  backgroundColor = Color.BLACK
  val renderer = new Group with SpriteBatchRenderer
  val font = internalFont("font/corbert.ttf", 30)//this must be disposed after using...
  val regions = font |> (fontToRegionMap(_)(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')))
  val sprites = "a" map regions map (new Sprite(_))
  renderer addDrawable sprites
  root add renderer

  override def dispose(){
    super.dispose()
    font.dispose()
  }
}
