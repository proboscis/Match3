package com.glyph._scala.game.builders

import com.glyph._scala.lib.libgdx.{BuilderOps, Builder}
import com.badlogic.gdx.assets.AssetManager
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.graphics.{Color, Texture}
import com.glyph._scala.game.action_puzzle.view.{ActionPuzzleTable}
import com.badlogic.gdx.Screen
import scala.language.implicitConversions
import scalaz._
import Scalaz._
import BuilderOps._
import com.glyph._scala.test.MenuScreen
import com.glyph._scala.test.AnimatedHolder2Test
import com.glyph._scala.game.action_puzzle.view.animated.{AnimatedPuzzleTable, Title}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.glyph._scala.game.action_puzzle.ComboPuzzle
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import com.glyph._scala.lib.injection.GLExecutionContext
import com.glyph._scala.lib.libgdx.GLFuture
/**
 * @author proboscis
 */
object Builders {
  private implicit class ResourceBuilder(name:String){
    def builder[T:ClassTag]:Builder[T] = new Builder[T]{
      def requirements: Builder.Assets = Set(implicitly[ClassTag[T]].runtimeClass->Seq(name))
      def create(implicit assets: AssetManager): T = assets.get[T](name)
    }
  }
  val darkHolo:Builder[Skin] = "skin/holo/Holo-dark-xhdpi.json".builder[Skin]
  val lightHolo:Builder[Skin] = "skin/holo/Holo-light-xhdpi.json".builder[Skin]
  val particleTexture:Builder[Texture] = "data/particle.png".builder[Texture]
  val dummyTexture:Builder[Texture] = "data/dummy.png".builder[Texture]
  val swordTexture:Builder[Texture] = "data/sword.png".builder[Texture]
  val roundRectTexture:Builder[Texture] = "data/round_rect.png".builder[Texture]
  val label:Skin=>String=>Label = skin => new Label(_:String,skin)
  val title = darkHolo map label map Title.apply
  def menuScreenBuilder[E]:(Seq[(String,E)],E=>Unit)=>Builder[Screen] = (elements,cb)=>{lightHolo map (skin => new MenuScreen[E](skin,elements,cb))}
  def actionPuzzleFunctionBuilder(game:()=>ComboPuzzle):Builder[()=>Future[AnimatedConstructor]] =
    (roundRectTexture |@| particleTexture |@| dummyTexture |@| darkHolo)(
      (a,b,c,d) =>()=> GLFuture(ActionPuzzleTable.animated(game())(a,b,c,d))
    )
  val screenBuilders = Map(
    "Mock"->AnimatedHolder2Test.builder
  )
}
