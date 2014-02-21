package com.glyph._scala.game.builders

import com.glyph._scala.lib.libgdx.{Builder, GLFuture}
import com.badlogic.gdx.assets.AssetManager
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.badlogic.gdx.Screen
import scala.language.implicitConversions
import com.glyph._scala.test.MenuScreen
import com.glyph._scala.test.AnimatedHolder2Test
import com.glyph._scala.game.action_puzzle.view.animated.{Menu, GameResult, Title}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager._
import scala.concurrent.Future
import com.glyph._scala.game.action_puzzle.{ColorTheme, ComboPuzzle}
import com.glyph._scala.lib.libgdx.actor.AnimatedTable
import com.glyph._scala.lib.util.extraction.Extractable
import com.glyph._scala.lib.libgdx.actor.transition.{LoadingAnimation, AnimatedExtractor}
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder._

import scalaz._
import Scalaz._
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph._scala.game.builders.Builders._
import scala.language.higherKinds
import com.glyph._scala.lib.libgdx.actor.widgets.Center
import com.glyph._scala.lib.util.Animated
import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * @author proboscis
 */
object Builders {

  private implicit class ResourceBuilder(name: String) {
    def builder[T: ClassTag]: Builder[T] = new Builder[T] {
      def requirements: Builder.Assets = Set(implicitly[ClassTag[T]].runtimeClass -> Seq(name))

      def create(implicit assets: AssetManager): T = assets.get[T](name)
    }
  }

  val darkHolo: Builder[Skin] = "skin/holo/Holo-dark-xhdpi.json".builder[Skin]
  val lightHolo: Builder[Skin] = "skin/holo/Holo-light-xhdpi.json".builder[Skin]
  val particleTexture: Builder[Texture] = "data/particle.png".builder[Texture]
  val dummyTexture: Builder[Texture] = "data/dummy.png".builder[Texture]
  val swordTexture: Builder[Texture] = "data/sword.png".builder[Texture]
  val roundRectTexture: Builder[Texture] = "data/round_rect.png".builder[Texture]
  val flat = (dummyTexture & darkHolo).map {
    case tex & holo =>
      new FlatSkin(
        ColorTheme.varyingColorMap(),
        c => new SpriteDrawable(new Sprite(tex) <| (_.setColor(c))),
        holo.getFont("default-font")
      )
  }
  val label: Skin => String => Label = skin => new Label(_: String, skin)
  val title = darkHolo map label map Title.apply

  def menuScreenBuilder[E]: (Seq[(String, E)], E => Unit) => Builder[Screen] = (elements, cb) => {
    lightHolo map (skin => new MenuScreen[E](skin, elements, cb))
  }

  def actionPuzzleFunctionBuilder(game: () => ComboPuzzle): Builder[() => Future[AnimatedConstructor]] =
    (roundRectTexture & particleTexture & dummyTexture & darkHolo) map {
      case a & b & c & d => () => GLFuture(ActionPuzzleTable.animated(game())(a, b, c, d))
    }

  val screenBuilders = Map(
    "Mock" -> AnimatedHolder2Test.builder
  )
  val puzzleBuilder = (roundRectTexture & particleTexture & dummyTexture & flat).map {
    case a & b & c & d => () => {
      GLFuture(ActionPuzzleTable.animated(new ComboPuzzle)(a, b, c, d))
    }
  }
  val menu = flat map Menu.constructor
}

object AnimatedConstructors {
  /*
  trait AnimatedExtractor[E] extends Actor with Animated{
  }
  implicit object
  */

  /**
   * this method requires some resources to be loaded
   * @param target
   * @param mapper
   * @param name
   * @param extractor
   * @param assets
   * @tparam E
   * @tparam T
   * @return
   */
  def extract[E[_], T]
  (target: E[T])
  (mapper: T => AnimatedConstructor)
  (name: String)
  (implicit extractor: Extractable[E], assets: AssetManager): AnimatedConstructor =
    info => callbacks => new AnimatedExtractor(info, callbacks, target, mapper) with LoadingAnimation[E, T] {
      override val loadingAnimation: AnimatedActor = new AnimatedTable {
        debug()
        //TODO use preloaded resources to show while loading
        Builders.darkHolo.load
        val actor = Center(new Label(name, Builders.darkHolo.create(assets)))
        add(actor).fill.expand
      }
    }

  def menu(implicit am:AssetManager,ex:Extractable[Builder]) = extract(Builders.menu)(a=>a)("loading menu")

  def title(implicit am: AssetManager, ex: Extractable[Builder]) =
    extract(Builders.title)(a => a)("loading")

  def result(implicit am: AssetManager, ex: Extractable[Builder]) =
    extract(flat map GameResult.constructor)(identity)("loading result")

  def puzzle(implicit am: AssetManager, ex1: Extractable[Builder], ex2: Extractable[({type l[A] = () => Future[A]})#l]) =
    extract(puzzleBuilder)(
      builder =>
        extract[({type l[A] = () => Future[A]})#l, AnimatedConstructor](builder)(a => a)
          ("initializing")
    )("loading")

}
