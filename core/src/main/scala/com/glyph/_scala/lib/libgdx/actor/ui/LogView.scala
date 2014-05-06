package com.glyph._scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.ui.{Label, WidgetGroup}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.pool.Poolable
import com.glyph._scala.lib.util.Disposable
import scala.collection.mutable.ArrayBuffer
import sun.rmi.runtime.Log
import scala.collection.mutable
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph._scala.lib.libgdx.screen.ConfiguredScreen
import com.glyph._scala.game.Glyphs
import com.badlogic.gdx.graphics.Texture
import com.glyph._scala.lib.libgdx.actor.SpriteActor
import com.badlogic.gdx.graphics.g2d.BitmapFont
import javax.annotation.Resources
import com.glyph._scala.game.builders.Builders
import com.glyph._scala.game.action_puzzle.view.animated.LazyAssets
import com.glyph._scala.lib.libgdx.actor.table.AssetManagerOps
import com.badlogic.gdx.assets.AssetManager

trait Log extends Actor with Disposable
/**
 * @author glyph
 */
class LogView extends WidgetGroup{
  val logs = mutable.Queue[Log]()
  val ROW = 5
  val slideDuration=0.1f
  def cellHeight = getHeight/ROW
  def <<(log:Log){
    logs.enqueue(log)
    addActor(log)
    if(logs.size > 6){
      val removing = logs.dequeue()
      removing.remove()
      removing.dispose()
    }
    log.setPosition(0,-cellHeight)
    //make it work first!!
    logs.zipWithIndex.foreach{
      case(l,i)=>
        import Actions._
        import com.badlogic.gdx.math.Interpolation._
        l.clearActions()//this works well when the animation is only applying to the actors.
        l.addAction(
            parallel(
              sizeTo(getWidth,cellHeight),
              moveTo(0,cellHeight*((logs.size-1)-i),slideDuration,exp5Out)
            )
        )
    }
  }
}
class LogViewTest extends ConfiguredScreen{
  import Glyphs._
  implicit val am = new AssetManager
  val skin = Builders.flat.forceCreate
  val sword:Texture = "data/sword.png"
  val logger = new LogView
  root.add(logger).fill.expand
  var counter = 0f
  val interval = 0.2f
  var i = 0
  override def render(delta: Float): Unit = {
    super.render(delta)
    counter += delta
    while(counter > interval){
      logger << new Label(s"$i",skin) with Log{
        override def dispose(): Unit = {}
      }
      log("added log to the log view")
      log(logger.logs)
      counter -= interval
      i += 1
    }
  }
}
