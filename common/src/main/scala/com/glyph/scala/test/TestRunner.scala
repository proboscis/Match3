package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.game.ScreenBuilderSupport
import com.glyph.scala.lib.libgdx.screen.{LoadingScreen, ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx
import com.badlogic.gdx.scenes.scene2d.ui.{List => GdxList, TextButton, ScrollPane, Skin}
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureAtlas}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx._
import com.glyph.scala.game.action_puzzle.screen.ActionPuzzleScreen
import scala.util.Try
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.font.FontUtil
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.glyph.scala.lib.libgdx.DrawFPS
import scalaz.Success
import com.badlogic.gdx.Input.Keys
import scala.collection.mutable
import com.glyph.scala.game.action_puzzle.view.ActionPuzzleTableScreen

/**
 * @author glyph
 */
class TestRunner(className: String) extends ScreenBuilderSupport with DrawFPS {

  import TestClass._
  lazy val font = FontUtil.internalFont("font/corbert.ttf", Gdx.graphics.getHeight / 20)
  def debugFont: BitmapFont = font
  def this() = this("")

  import ScreenBuilder._
  //typeOf[Int] <:< typeOf[String]
  val screenStack = mutable.Stack[Screen]()
  val screenPopProcessor = new InputAdapter {
    override def keyDown(keycode: Int): Boolean = {
      if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
        popScreen()
        true
      } else super.keyDown(keycode)
    }
  }

  override def setScreen(screen: Screen): Unit = {
    if(getScreen != null){
      screenStack.push(getScreen)
    }
    setScreenWithProcessor(screen)
  }

  override def resume(): Unit = {
    println("resume!")
    if(!assetManager.update()){
      setScreenWithProcessor(new LoadingScreen(() => {
        pausedScreen foreach setScreenWithProcessor
      }, assetManager))
    }else{
      pausedScreen foreach setScreenWithProcessor
    }
    pausedScreen = None
  }

  def setScreenWithProcessor(screen:Screen){
    log("screen stack:"+screenStack.map(_.getClass.getSimpleName))
    Gdx.input.setInputProcessor(screenPopProcessor)
    super.setScreen(screen)
    val screenInputProcessor = Gdx.input.getInputProcessor
    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(screenPopProcessor)
    multiplexer.addProcessor(screenInputProcessor)
    Gdx.input.setInputProcessor(multiplexer)
  }
  def popScreen(){
    log("pop screen"+screenStack.map(_.getClass.getSimpleName))
    if (!screenStack.isEmpty) {
      screenStack.pop() match {
        case s:LoadingScreen => popScreen()
        case s => setScreenWithProcessor(s)
      }
    }else{
      log("exit app")
      Gdx.app.exit()
    }
  }

  override def create() {
    super.create()
    Gdx.input.setCatchBackKey(true)

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
