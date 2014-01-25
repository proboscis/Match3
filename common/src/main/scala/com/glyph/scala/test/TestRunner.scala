package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.game.ScreenBuilderSupport
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, LoadingScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx._
import scala.util.Try
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.font.FontUtil
import com.glyph.scala.lib.libgdx.{Builder, DrawFPS}
import com.badlogic.gdx.Input.Keys
import scala.collection.mutable
import com.glyph.scala.lib.util.{Animated, Logging}
import com.glyph.scala.lib.util.reactive.{Var, Varying, VClass, Reactor}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder
import com.glyph.scala.game.builders.Builders

/**
 * @author glyph
 */
class TestRunner(className: String)
  extends ScreenBuilderSupport
  with DrawFPS
  with Popped
  with Reactor {

  lazy val font = FontUtil.internalFont("font/corbert.ttf", Gdx.graphics.getHeight / 20)

  def debugFont: BitmapFont = font

  def this() = this("")


  //typeOf[Int] <:< typeOf[String]
  //TODO this function cannot be done without a class tag.
  def classToVSB(cls: Class[_]): Varying[Option[ScreenBuilder]] = cls match {
    case c if classOf[ScreenBuilder].isAssignableFrom(c) =>
      VClass[ScreenBuilder](c.getCanonicalName).map(_.map(_.newInstance()))
    case c if classOf[Screen].isAssignableFrom(c) => VClass[Screen](c.getCanonicalName).map(_.map(
      scls => new ScreenBuilder {
        def requirements: Set[(Class[_], Seq[String])] = Set()

        def create(implicit assetManager: AssetManager): Screen = scls.newInstance()
      }
    ))
    case c if classOf[Builder[Actor with Animated]].isAssignableFrom(c) => VClass[Builder[Actor with Animated]](c.getCanonicalName).map{
      _.map{
        c=>new ScreenBuilder{
          val builder = c.newInstance()
          def requirements: Set[(Class[_], Seq[String])] = builder.requirements

          def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
            val holder = new AnimatedBuilderHolder{}
            root.add(holder).fill.expand
            holder.push(builder)
          }
        }
      }
    }
  }

  override def create() {
    super.create()
    Gdx.input.setCatchBackKey(true)
    className match {
      case "" =>
        //TODO make this work with VClass
        setBuilder(Builders.menuScreenBuilder(TestClass.builders,setBuilder))
        /*
        setBuilder(new MenuScreenBuilder {
        override def create(implicit assets: AssetManager): MenuScreen = {
          val result = super.create(assets)
          result.onLaunch = cls => {
            clearReaction()
            reactSome(classToVSB(cls)) {
              b =>
                popScreen(exit = false)
                setBuilder(b)
            }
          }
          result
        }
      })*/
      case c => reactSome(classToVSB(Class.forName(c))) {
        b =>
          popScreen(exit = false)
          setBuilder(b)
      }
    }
    Gdx.app.addLifecycleListener(new LifecycleListener {
      def dispose(){
        System.exit(0)
      }

      def pause(): Unit = {}

      def resume(): Unit = {}
    })
  }

  def tryToVnel2[T](t: Try[T]): ValidationNel[Throwable, T] = t match {
    case scala.util.Success(s) => s.successNel
    case scala.util.Failure(f) => f.failNel
  }
}

trait Pop extends Game with Logging {
  val screenStack = mutable.Stack[Screen]()
  val screenPopProcessor = new InputAdapter {
    override def keyDown(keycode: Int): Boolean = {
      if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
        popScreen()
        true
      } else false
    }
  }

  override def setScreen(screen: Screen): Unit = {
    if (getScreen != null) {
      screenStack.push(getScreen)
    }
    setScreenWithProcessor(screen)
  }

  def setScreenWithProcessor(screen: Screen) {
    log("screen stack:" + screenStack.map(_.getClass.getSimpleName))
    Gdx.input.setInputProcessor(screenPopProcessor)
    super.setScreen(screen)
    val screenInputProcessor = Gdx.input.getInputProcessor
    val multiplexer = new InputMultiplexer()
    multiplexer.addProcessor(screenPopProcessor)
    multiplexer.addProcessor(screenInputProcessor)
    Gdx.input.setInputProcessor(multiplexer)
  }

  def popScreen(exit: Boolean = true) {
    log("pop screen" + screenStack.map(_.getClass.getSimpleName))
    if (!screenStack.isEmpty) {
      screenStack.pop() match {
        case s: LoadingScreen => popScreen(exit)
        case s => setScreenWithProcessor(s)
      }
    } else if (exit) {
      log("exit app")
      Gdx.app.exit()
    }
  }
}

trait Popped extends ScreenBuilderSupport with Pop {
  override def resume(): Unit = {
    println("resume!")
    val current = getScreen
    if (current != null) {
      if (!assetManager.update()) {
        setScreenWithProcessor(new LoadingScreen(() => {
          setScreenWithProcessor(current)
        }, assetManager))
      } else {
        setScreenWithProcessor(current)
      }
    }
  }
}