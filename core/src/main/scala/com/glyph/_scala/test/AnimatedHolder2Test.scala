package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.{BuilderOps, GLFuture, BuilderExtractor2, Builder}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.actor.transition._
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.{Info, Callbacks, AnimatedConstructor, AnimatedGraph}
import com.glyph._scala.lib.libgdx.actor.{AnimatedTable, SpriteActor, Tasking}
import com.glyph._scala.lib.util.extraction.{ExtractableFuture, ExtractableFunctionFuture}
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.action_puzzle.view.animated.{LazyAssets, GameResult, Menu, Title}
import com.glyph._scala.lib.libgdx.game.LimitDelta
import com.glyph._scala.lib.util.json.GdxJSON
import com.glyph._scala.game.builders.Builders._
import com.glyph._scala.game.action_puzzle.view.animated.Title.TitleStyle
import com.glyph._scala.lib.libgdx.BuilderOps.&
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.glyph._scala.game.action_puzzle.ComboPuzzle
import com.glyph._scala.lib.util.{Graph, Logging}
import com.glyph._scala.lib.util.reactive.{VClass, VClassGenerator}
import com.glyph._scala.game.Glyphs
import com.badlogic.gdx.graphics.Texture

object AnimatedHolder2Test {
  val builder = Builder(Set(), assets => new MockTransition {
    override implicit def assetManager = assets

    manager.start(title, Map(), push)

  })
}

trait AnimatedRunner extends ConfiguredScreen {
  def graph: AnimatedGraph
  //beware of second asset manager!
  implicit def assetManager: AssetManager

  implicit val holder = new StackedAnimatedActorHolder with Tasking {} <| (root.add(_).fill.expand)
  implicit val extractableBuilder = new BuilderExtractor2
  implicit val extractableFF = ExtractableFunctionFuture
  implicit val extractableFuture = ExtractableFuture
  lazy val manager = new AnimatedManager(graph)
}

trait MockTransition
  extends AnimatedRunner
  with AnimatedConstructors
  with LimitDelta {
  val push = holder.push _
  val switch = holder.switch _
  import AnimatedConstructorOps._

  override def graph: AnimatedGraph = Map(
    title -> Map(
      "dummy" ->(push, menu)
    ),
    menu -> Map(
      "Title" ->(switch, title),
      "Menu" ->(switch, menu),
      "Puzzle" ->(push, puzzle)
    ),
    puzzle -> Map(
      "game_over" ->(switch, result)
    ),
    result -> Map(
      "replay" ->(switch, puzzle),
      "title" ->(switch, title)
    )
  ) ++ Map(
    constructorSelector->constructors.mapValues(ac => (push,ac))
    )
  lazy val constructorSelector:AnimatedConstructor =flat.map(
    skin =>((info:Info)=>(callbacks:Callbacks)=> {
      val selector = new StringSelector(skin,constructors.keys,str => callbacks(str)(Map()))
      AnimatedTable.apply(_.fill.expand)(selector)
    }):AnimatedConstructor).extract

}
class MockSelector extends MockTransition with LazyAssets{
  manager.start(constructorSelector,Map(),holder.push)
}

trait AnimatedConstructors extends DefaultExtractors with Logging{
  import AnimatedConstructorOps._
  val styleJson = GdxJSON("comboPuzzle/style.json")
  val menuStyle = styleJson.map{
    json => for{
      padding <- json.padding.as[Float]
      space <- json.space.as[Float]
    } yield flat map (skin => Menu.Style(padding = padding,space = space,skin = skin))
  }
  val resultStyle = for(json <- styleJson) yield for{
    padding <- json.padding.as[Float]
    space <- json.space.as[Float]
  } yield for (skin <- flat) yield GameResult.Style(padding,space,skin)
  val titleStyle = for(json <- styleJson) yield for{
    padding <- json.padding.as[Float]
    space <- json.space.as[Float]
  } yield (flat&roundRectTexture) map {
      case skin&roundTex => TitleStyle(margin = padding,space = space,titleFont = skin.getFont("default-font"),roundTex,skin)
    }
  val menu = menuStyle.map(_.map(_.map(Menu.constructor))).map(_.map(_.extract).extract).extract
  val title = titleStyle.map(_.map(_.map(Title.third).extract).extract).extract
  import VClass._
  import Glyphs.getClassMacro
  val result = resultStyle.map(_.map(_.map(style => VClass[AnimatedConstructor,GameResult].newInstance(Typed(style)).map(_.extract).extract).extract).extract).extract
  val puzzle = (roundRectTexture & particleTexture & dummyTexture & flat).map {
    case a & b & c & d => () => {
      // why the hell is this called twice!?
      err("called puzzle constructor")
      GLFuture(ActionPuzzleTable.animated(new ComboPuzzle)(a, b, c, d))
    }
  }.map((_: FF[AnimatedConstructor]).extract).extract
  def textureBuilderToMock(builder:Builder[Texture]):AnimatedConstructor = builder.map{
    mockTex => new SpriteActor(mockTex) |> AnimatedConstructor.apply
  }.extract
  def fileToMock(fileName:String):AnimatedConstructor = Builder[Texture](fileName) |> textureBuilderToMock
  lazy val resultMock:AnimatedConstructor = "data/mock/gameResult.png" |> fileToMock
  lazy val resultMock2:AnimatedConstructor = "data/mock/gameResult2.png" |> fileToMock

  lazy val constructors: String Map AnimatedConstructor = Map(
    "menu"->menu,
    "title"->title,
    "result"->result,
    "puzzle"->puzzle,
    "resultMock1"->resultMock,
    "resultMock2"->resultMock2
  )
}