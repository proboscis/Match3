package com.glyph.scala.test

import com.glyph.scala.game.builders.Builders
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder2
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedBuilder
import com.glyph.scala.game.action_puzzle.view.animated.Menu

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
    val holder = new AnimatedBuilderHolder2{} <| (root.add(_).fill.expand)
    val title = Builders.title
    val menu = Builders.darkHolo map Menu.constructor
    val push = holder.push(_:AnimatedBuilder)(assets)
    val manager = new AnimatedManager(
      Map(
        title->Map("dummy"->(push,menu)),
        menu->Map(
          "1"->(push,title),
          "2"->(push,menu))
      )
    )
    manager.start(title,Map(),push)
  })
}