package com.glyph._scala.game.builders

import com.glyph._scala.lib.libgdx.{Builder, GLFuture}
import com.badlogic.gdx.assets.{AssetDescriptor, AssetManager}
import scala.reflect.ClassTag
import com.badlogic.gdx.scenes.scene2d.ui.{Label, Skin}
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.badlogic.gdx.Screen
import scala.language.implicitConversions
import com.glyph._scala.test.MenuScreen
import com.glyph._scala.test.AnimatedHolder2Test
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager._
import scala.concurrent.Future
import com.glyph._scala.game.action_puzzle.{ColorTheme, ComboPuzzle}

import scalaz._
import Scalaz._
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.graphics.g2d.{NinePatch, BitmapFont}
import scala.language.higherKinds
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor

/**
"Mock" -> AnimatedHolder2Test.builder
  * @author proboscis
  */
object Builders {

  private implicit class ResourceBuilder(name: String) {
    def builder[T: Class]: Builder[T] = new Builder[T] {
      override def requirements: Seq[AssetDescriptor[_]] = new AssetDescriptor(name,implicitly[Class[T]])::Nil

      def create(implicit assets: AssetManager): T = assets.get[T](name)
    }
  }

  lazy val corbert2 = FontUtil.internalFont("font/corbert.ttf", 100)
  val corbert: Builder[BitmapFont] = "font/corbert.fnt".builder[BitmapFont]
  val darkHolo: Builder[Skin] = "skin/holo/Holo-dark-xhdpi.json".builder[Skin]
  val lightHolo: Builder[Skin] = "skin/holo/Holo-light-xhdpi.json".builder[Skin]
  val particleTexture: Builder[Texture] = "data/particle.png".builder[Texture]
  val dummyTexture: Builder[Texture] = "data/dummy.png".builder[Texture]
  val swordTexture: Builder[Texture] = "data/sword.png".builder[Texture]
  val roundRectTexture: Builder[Texture] = "data/rr160.png".builder[Texture]
  val roundRectNP = roundRectTexture.map {
    tex => val w = tex.getWidth / 3
      new NinePatch(tex, w, w, w, w)
  }
  val flat = (roundRectNP).map {
    case np =>
      new FlatSkin(
        ColorTheme.varyingColorMap(),
        c => new NinePatchDrawable(new NinePatch(np) <| (_.setColor(c))),
        corbert2
      )
  }
  val label: Skin => String => Label = skin => new Label(_: String, skin)

  def menuScreenBuilder[E]: (Seq[(String, E)], E => Unit) => Builder[Screen] = (elements, cb) => {
    lightHolo map (skin => new MenuScreen[E](skin, elements, cb))
  }

  def actionPuzzleFunctionBuilder(game: () => ComboPuzzle)(implicit processor:ParallelProcessor): Builder[() => Future[AnimatedConstructor]] =
    (roundRectTexture & particleTexture & dummyTexture & flat) map {
      case a & b & c & d => () => GLFuture(ActionPuzzleTable.animated(game())(a, b, c, d))
    }

  val screenBuilders = Map("Mock" -> AnimatedHolder2Test.builder)
}