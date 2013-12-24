package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.game.ScreenBuilderSupport
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx
import com.badlogic.gdx.scenes.scene2d.ui.{List => GdxList, TextButton, ScrollPane, Skin}
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureAtlas}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.{Screen, Gdx}
import com.glyph.scala.game.action_puzzle.screen.ActionPuzzleScreen
import scala.util.Try
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.font.FontUtil
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.glyph.scala.lib.libgdx.DrawFPS

/**
 * @author glyph
 */
class TestRunner(className: String) extends ScreenBuilderSupport with DrawFPS {

  lazy val font = FontUtil.internalFont("font/corbert.ttf", Gdx.graphics.getHeight / 20)

  def debugFont: BitmapFont = font

  def this() = this("")

  type ->[A, B] = (A, B)

  import ScreenBuilder._

  //typeOf[Int] <:< typeOf[String]

  override def create() {
    super.create()
    val builderClasses =
        classOf[ActionPuzzleScreen] ::
        classOf[TrailedParticleTest] ::
        classOf[ParticleTest]::
        classOf[UVTrailTest] ::
        classOf[ImmediateTest] ::
        classOf[WordParticle] :: Nil
    val files = "screens/action.js" :: "screens/puzzle.js" :: Nil
    val screenClasses =
        classOf[ShaderRotationTest] ::
        classOf[ExplosionTest] ::
        classOf[MeshTest] ::
        classOf[TrailTest] ::
        classOf[EffectTest] ::
        classOf[FrameBufferTest] ::
        classOf[WindowTest] ::
        classOf[ComboEffect] ::
        Nil

    val classBuilders = builderClasses map (c => c.newInstance() -> c.getSimpleName)
    val fileBuilders = files map {
      f => createFromJson(f) -> f
    } collect {
      case (Success(s), f) => s -> f
    }
    val pkgBuilders = screenClasses map {
      clazz => new ScreenBuilder {
        def requiredAssets: Set[(Class[_], Seq[String])] = Set()

        def create(assetManager: AssetManager): Screen = clazz.newInstance()
      } -> clazz.getSimpleName
    }
    val builders: Seq[ScreenBuilder -> String] = classBuilders ++ pkgBuilders ++ fileBuilders
    className match {
      case "" => setBuilder(new MenuScreen)
      case c => setBuilder(Class.forName(c).newInstance() match {
        case builder: ScreenBuilder => builder
        case screen: com.badlogic.gdx.Screen => new ScreenBuilder {
          def requiredAssets: Set[(Class[_], Seq[String])] = Set()

          def create(assetManager: AssetManager) = screen
        }
      })
    }

    class MenuScreen extends ScreenBuilder {

      def requiredAssets: Set[(Class[_], Seq[String])] = Set(
        classOf[TextureAtlas] -> ("skin/default.atlas" :: Nil),
        classOf[Skin] -> ("skin/holo/Holo-dark-xhdpi.json" :: Nil)
      )

      def create(assets: AssetManager): gdx.Screen = new ConfiguredScreen {
        debug() = false
        backgroundColor = Color.BLACK
        val skin = assets.get[Skin]("skin/holo/Holo-dark-xhdpi.json")
        val tbStyle = skin.get("default", classOf[TextButtonStyle])
        val list = builders.map {
          case (builder, name) => name
        }.toArray[Object] |> (new GdxList(_, skin))
        val button = new TextButton("launch", skin)
        button.addListener(new ChangeListener {
          def changed(p1: ChangeEvent, p2: Actor) {
            builders(list.getSelectedIndex)._1 |> setBuilder
          }
        })
        val scrolling = new ScrollPane(list, skin)
        scrolling.setScrollingDisabled(false, false)
        root.add(scrolling).fill.expand(1, 9).row
        root.add(button).fill.expand(1, 1)

      }
    }

  }

  def tryToVnel2[T](t: Try[T]): ValidationNel[Throwable, T] = t match {
    case scala.util.Success(s) => s.successNel
    case scala.util.Failure(f) => f.failNel
  }
}
