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
import com.glyph._scala.game.action_puzzle.view.animated.{Menu, Title}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager._
import scala.concurrent.Future
import com.glyph._scala.game.action_puzzle.{ColorTheme, ComboPuzzle}
import com.glyph._scala.lib.util.extraction.Extractable
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedExtractor
import com.glyph._scala.lib.libgdx.actor.table.AnimatedBuilderHolder._

import scalaz._
import Scalaz._
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.badlogic.gdx.scenes.scene2d.utils.{NinePatchDrawable, SpriteDrawable}
import com.badlogic.gdx.graphics.g2d.{NinePatch, BitmapFont, Sprite}
import com.glyph._scala.game.builders.Builders._
import scala.language.higherKinds
import com.glyph._scala.lib.util.Animated

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

  val corbert: Builder[BitmapFont] = "font/corbert.fnt".builder[BitmapFont]
  val darkHolo: Builder[Skin] = "skin/holo/Holo-dark-xhdpi.json".builder[Skin]
  val lightHolo: Builder[Skin] = "skin/holo/Holo-light-xhdpi.json".builder[Skin]
  val particleTexture: Builder[Texture] = "data/particle.png".builder[Texture]
  val dummyTexture: Builder[Texture] = "data/dummy.png".builder[Texture]
  val swordTexture: Builder[Texture] = "data/sword.png".builder[Texture]
  val roundRectTexture: Builder[Texture] = "data/round_rect.png".builder[Texture]
  val roundRectNP = roundRectTexture.map {
    tex => val w = tex.getWidth / 3
      new NinePatch(tex, w, w, w, w)
  }
  val flat = (corbert & roundRectNP).map {
    case font & np =>
      new FlatSkin(
        ColorTheme.varyingColorMap(),
        c => new NinePatchDrawable(new NinePatch(np) <| (_.setColor(c))),
        font
      )
  }
  val label: Skin => String => Label = skin => new Label(_: String, skin)

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
}