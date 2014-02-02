package com.glyph.scala.test

import com.glyph.scala.game.builders.Builders
import com.glyph.scala.lib.libgdx.Builder
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder2
import com.glyph.scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph.scala.lib.libgdx.actor.table.AnimatedBuilderHolder.AnimatedBuilder

/**
 * @author glyph
 */
class AnimatedHolder2Test {

}
object AnimatedHolder2Test{
  import scalaz._
  import Scalaz._
  val builder = Builder(Set(),assets =>new ConfiguredScreen{
    implicit val _ = assets
    val holder = new AnimatedBuilderHolder2{} <| (root.add(_).fill.expand)
    val title = Builders.title
    val push = holder.push(_:AnimatedBuilder)(assets)
    val manager = new AnimatedManager(Map(title->Map("dummy"->(push,title))))
    manager.start(title,Map(),push)
  })
}