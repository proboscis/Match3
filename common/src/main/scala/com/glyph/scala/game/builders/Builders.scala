package com.glyph.scala.game.builders

import com.glyph.scala.lib.libgdx.{BuilderOps, Builder}
import com.badlogic.gdx.assets.AssetManager
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.graphics.{Color, Texture}
import com.glyph.scala.game.action_puzzle.view.{ActionPuzzleTable}
import com.badlogic.gdx.Screen
import scala.language.implicitConversions
import scalaz._
import Scalaz._
import BuilderOps._
import com.glyph.scala.test.MenuScreen
import com.glyph.scala.test.AnimatedHolder2Test
import com.glyph.scala.game.action_puzzle.view.animated.Title

/**
 * @author proboscis
 */
object Builders {
  implicit class ResourceBuilder(name:String){
    def builder[T:ClassTag]:Builder[T] = new Builder[T]{
      def requirements: Builder.Assets = Set(implicitly[ClassTag[T]].runtimeClass->Seq(name))
      def create(implicit assets: AssetManager): T = assets.get[T](name)
    }
  }
  val darkHolo = "skin/holo/Holo-dark-xhdpi.json".builder[Skin]
  val lightHolo = "skin/holo/Holo-light-xhdpi.json".builder[Skin]
  val particleTexture = "data/particle.png".builder[Texture]
  val dummyTexture = "data/dummy.png".builder[Texture]
  val swordTexture = "data/sword.png".builder[Texture]
  val roundRectTexture = "data/round_rect.png".builder[Texture]
  val label:Skin=>String=>Label = skin => new Label(_:String,skin)
  val title = darkHolo map label map Title.apply
  def menuScreenBuilder[E]:(Seq[(String,E)],E=>Unit)=>Builder[Screen] = (elements,cb)=>{lightHolo map (skin => new MenuScreen[E](skin,elements,cb))}
  val actionPuzzleBuilder:Builder[ActionPuzzleTable] = (roundRectTexture |@| particleTexture |@| dummyTexture  |@| lightHolo )(new ActionPuzzleTable(_,_,_,_))
  val actionPuzzleScreenBuilder:Builder[Screen] = actionPuzzleBuilder map ActionPuzzleTable.toScreen
  val screenBuilders = Map(
    "ActionPuzzle"->actionPuzzleScreenBuilder,
    "AnimatedHolder2"->AnimatedHolder2Test.builder
  )
}
