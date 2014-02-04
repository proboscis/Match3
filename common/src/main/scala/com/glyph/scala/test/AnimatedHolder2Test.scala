package com.glyph.scala.test

import com.glyph.scala.game.builders.Builders
import com.glyph.scala.lib.libgdx.Builder
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.libgdx.actor.transition.{LoadingAnimation, StackedAnimatedActorHolder, AnimatedBuilderExtractor, AnimatedManager}
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.glyph.scala.game.action_puzzle.view.animated.{AnimatedTable, AnimatedPuzzleTable, Menu}
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.graphics.{Color, Texture}
import com.glyph.scala.lib.libgdx.actor.SpriteActor

/**
 * @author glyph
 */
class AnimatedHolder2Test {

}

object AnimatedHolder2Test {
  //TODO make unit test for animated classes

  import scalaz._
  import Scalaz._

  val builder = Builder(Set(classOf[Texture] -> Seq("data/dummy.png")), assets => new ConfiguredScreen {
    implicit val _ = assets

    def extract(builder: Builder[AnimatedConstructor]): AnimatedConstructor =
      info => callbacks => new AnimatedBuilderExtractor(info, callbacks, builder)(assets) with LoadingAnimation {
        override val loadingAnimation: AnimatedActor = new AnimatedTable {
          debug()
          val actor = new SpriteActor().setup(Builders.dummyTexture.create(assets))
          actor.setColor(Color.RED)
          add(actor).fill.expand
        }
      }

    val holder = new StackedAnimatedActorHolder {} <| (root.add(_).fill.expand)
    val title = extract(Builders.title)
    val menu = extract(Builders.lightHolo map Menu.constructor)
    val puzzle = extract(Builders.actionPuzzleBuilder map AnimatedPuzzleTable.animated)
    val push = holder.push(_: AnimatedActor)
    val manager = new AnimatedManager(
      Map(
        title -> Map("dummy" ->(push, menu)),
        menu -> Map(
          "1" ->(push, title),
          "2" ->(push, menu),
          "3" ->(push, puzzle)
        )
      )
    )
    manager.start(title, Map(), push)
  })
}