package com.glyph.scala.test

import com.glyph.scala.game.builders.Builders
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder2
import com.glyph.scala.lib.libgdx.actor.transition.{StackedAnimatedActorHolder, AnimatedBuilderExtractor, AnimatedManager}
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.{AnimatedActor, AnimatedBuilder}
import com.glyph.scala.game.action_puzzle.view.animated.{AnimatedPuzzleTable, Menu}
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor

/**
 * @author glyph
 */
class AnimatedHolder2Test {

}
object AnimatedHolder2Test{
  //TODO make unit test for animated classes
  import scalaz._
  import Scalaz._
  val builder = Builder(Set(),assets =>new ConfiguredScreen{
    implicit val _ = assets
    def extract(builder:Builder[AnimatedConstructor]):AnimatedConstructor = info => callbacks => new AnimatedBuilderExtractor(info,callbacks,builder)(assets)
    val holder = new StackedAnimatedActorHolder{} <| (root.add(_).fill.expand)
    val title = extract(Builders.title)
    val menu = extract(Builders.darkHolo map Menu.constructor)
    val puzzle = extract(Builders.actionPuzzleBuilder map AnimatedPuzzleTable.animated)
    val push = holder.push(_:AnimatedActor)
    val manager = new AnimatedManager(
      Map(
        title->Map("dummy"->(push,menu)),
        menu->Map(
          "1"->(push,title),
          "2"->(push,menu),
          "3"->(push,puzzle)
        )
      )
    )
    manager.start(title,Map(),push)
  })
}