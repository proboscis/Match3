package com.glyph.scala.test

import com.glyph.scala.game.builders.Builders
import com.glyph.scala.lib.libgdx.{BuilderExtractor2, Builder}
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.libgdx.actor.transition.{LoadingAnimation, StackedAnimatedActorHolder, AnimatedExtractor, AnimatedManager}
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedActor
import com.glyph.scala.game.action_puzzle.view.animated.{AnimatedTable, AnimatedPuzzleTable, Menu}
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.badlogic.gdx.graphics.{Color, Texture}
import com.glyph.scala.lib.libgdx.actor.{Tasking, SpriteActor}
import com.glyph.scala.lib.util.extraction.{ExtractableFunctionFuture, ExtractableFuture, Extractable, ExtractableFunction0}
import com.badlogic.gdx.assets.AssetManager
import scala.concurrent.Future

object AnimatedHolder2Test {
  //TODO make unit test for animated classes

  import scalaz._
  import Scalaz._

  def extract[E[_],T](builder: E[T])(mapper:T=>AnimatedConstructor)(implicit extractor:Extractable[E], assets:AssetManager): AnimatedConstructor =
    info => callbacks => new AnimatedExtractor(info, callbacks, builder,mapper) with LoadingAnimation[E,T] {
      override val loadingAnimation: AnimatedActor = new AnimatedTable {
        debug()
        val actor = new SpriteActor().setup(Builders.dummyTexture.create)
        actor.setColor(Color.RED)
        add(actor).fill.expand
      }
    }

  val builder = Builder(Set(classOf[Texture] -> Seq("data/dummy.png")), assets => new ConfiguredScreen {
    val holder = new StackedAnimatedActorHolder with Tasking {} <| (root.add(_).fill.expand)
    implicit val _1 = assets
    implicit val _2 = holder
    implicit val builderExtractor = new BuilderExtractor2
    implicit val functionExtractor = ExtractableFunctionFuture
    val title = extract(Builders.title)(a=>a)
    val menu = extract(Builders.lightHolo map Menu.constructor)(a=>a)
    val puzzleBuilder = Builders.actionPuzzleFunctionBuilder
    //you need to specify the type lambda since the compiler cannot infer the nested higher kinded types.
    val puzzle = extract(puzzleBuilder)(builder=>extract[({type l[A] = ()=>Future[A]})#l,AnimatedConstructor](builder)(a=>a))
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
