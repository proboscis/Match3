package com.glyph._scala.test

import com.glyph._scala.game.builders.AnimatedConstructors
import com.glyph._scala.lib.libgdx.{BuilderExtractor2, Builder}
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.lib.libgdx.actor.transition.{StackedAnimatedActorHolder, AnimatedManager}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedGraph
import com.glyph._scala.lib.libgdx.actor.Tasking
import com.glyph._scala.lib.util.extraction.ExtractableFunctionFuture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager

object AnimatedHolder2Test {
  val builder = Builder(Set(classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")), assets => new MockTransition {
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


trait MockTransition extends AnimatedRunner {
  private implicit val _1 = builderExtractor
  private implicit val _2 = functionExtractor
  val menu = AnimatedConstructors.menu

  val title = AnimatedConstructors.title
  val result = AnimatedConstructors.result
  val puzzle = AnimatedConstructors.puzzle
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
trait AnimatedMock extends MockTransition{
  //beware of second asset manager!
  override implicit def assetManager: AssetManager = new AssetManager
}