package com.glyph._scala.test

import com.glyph._scala.game.builders.Builders
import com.glyph._scala.lib.libgdx.{BuilderOps, GLFuture, BuilderExtractor2, Builder}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.actor.transition.{MonadicAnimated, StackedAnimatedActorHolder, AnimatedManager}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.{AnimatedConstructor, AnimatedGraph}
import com.glyph._scala.lib.libgdx.actor.{SpriteActor, AnimatedTable, Tasking}
import com.glyph._scala.lib.util.extraction.{Extractable, ExtractableFunctionFuture}
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph._scala.game.action_puzzle.view.animated.{GameResult, Menu, Title}
import com.glyph._scala.lib.libgdx.game.LimitDelta
import scala.concurrent.Future
import com.glyph._scala.game.action_puzzle.view.ActionPuzzleTable
import com.glyph._scala.game.action_puzzle.ComboPuzzle

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
  implicit val builderExtractor = new BuilderExtractor2
  implicit val functionExtractor = ExtractableFunctionFuture
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

trait AnimatedConstructors {
  type FF[A] = () => Future[A]

  implicit def builderExtractor: Extractable[Builder]

  implicit def functionExtractor: Extractable[FF]

  implicit def assetManager: AssetManager

  import Builders._
  import BuilderOps._
  import MonadicAnimated._

  val splashAnimation = swordTexture.map {
    tex => new AnimatedTable {
      add(new SpriteActor(tex)).fill.expand
      override def in(cb: () => Unit): Unit = {
        log("splash screen in")
        super.in(cb)
      }
    }
  } <| (_.load) |> (_.create)
  val menu = extract(flat map(skin => Menu.Style(skin = skin)) map Menu.constructor)(splashAnimation) |> toAnimatedConstructor
  val title = extract(darkHolo & roundRectTexture)(splashAnimation).map {
    case skin & rect => val style = Title.TitleStyle(titleFont = skin.getFont("default-font"), roundTex = rect, skin = skin)
      Title.third(style)
  } |> toAnimatedConstructor
  val result = flat.map(skin => GameResult.Style(skin = skin) |> GameResult.constructor) |> (extract(_)(splashAnimation)) |> toAnimatedConstructor
  val puzzle = (roundRectTexture & particleTexture & dummyTexture & flat).map {
    case a & b & c & d => () => {
      GLFuture(ActionPuzzleTable.animated(new ComboPuzzle)(a, b, c, d))
    }
  } |> (extract(_)(splashAnimation).flatMap(
    extract[FF, AnimatedConstructor](_)(splashAnimation)
  )) |> toAnimatedConstructor
}