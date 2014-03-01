package com.glyph._scala.test

import com.glyph._scala.lib.libgdx.{GLFuture, BuilderExtractor2, Builder}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.actor.transition._
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.{AnimatedConstructor, AnimatedGraph}
import com.glyph._scala.lib.libgdx.actor.Tasking
import com.glyph._scala.lib.util.extraction.ExtractableFunctionFuture
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.action_puzzle.view.animated.{GameResult, Menu, Title}
import com.glyph._scala.lib.libgdx.game.LimitDelta
import com.glyph._scala.lib.util.json.GdxJSON
import com.glyph._scala.game.builders.Builders._
import com.glyph._scala.game.action_puzzle.view.animated.Title.TitleStyle
import com.glyph._scala.lib.libgdx.BuilderOps.&
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.glyph._scala.game.action_puzzle.ComboPuzzle
import com.glyph._scala.lib.util.Logging

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
  lazy val manager = new AnimatedManager(graph)
}


trait MockTransition
  extends AnimatedRunner
  with AnimatedConstructors
  with LimitDelta {
  val push = holder.push _
  val switch = holder.switch _

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
  )
}

trait AnimatedMock extends MockTransition {
  //beware of second asset manager!
  override implicit def assetManager: AssetManager = new AssetManager
}

trait AnimatedConstructors extends DefaultExtractors with Logging{
  import AnimatedConstructorOps._
  val menu = (flat map (skin => Menu.Style(skin = skin)) map Menu.constructor).extract
  val title = GdxJSON("comboPuzzle/titleStyle.json").map {
    json => for {
      margin <- json.margin.as[Float]
      space <- json.space.as[Float]
    } yield {
      (darkHolo & roundRectTexture).map {
        case skin & rect => {
          Title.third(TitleStyle(
            titleFont = skin.getFont("default-font"),
            space = space,
            roundTex = rect,
            skin = skin))
        }
      }
    }//TODO これをなんとかする方法はないのか？
  }.map(_.map(_.extract).extract).extract// you have to write three type-lambda to delete these....
  val result = flat.map(skin => GameResult.Style(skin = skin) |> GameResult.constructor).extract
  val puzzle = (roundRectTexture & particleTexture & dummyTexture & flat).map {
    case a & b & c & d => () => {
      // why the hell is this called twice!?
      err("called puzzle constructor")
      GLFuture(ActionPuzzleTable.animated(new ComboPuzzle)(a, b, c, d))
    }
  }.map((_: FF[AnimatedConstructor]).extract).extract
}