package com.glyph._scala.lib.libgdx.actor.action

import scalaz._
import Scalaz._
import com.glyph._scala.lib.libgdx.actor.{Updating, Tasking}
import com.glyph._scala.lib.util.updatable.task._
import com.glyph._scala.lib.util.animator.{AnimatedFloat2, Swinger}
import com.glyph._scala.lib.util.{Threading, Logging}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.updatable.Updatable
import com.glyph._scala.lib.util.pool.Pool

/**
 * @author glyph
 */
trait Shivering extends Updatable with Logging with Threading{
  private var started = false
  private val shiverProcessor = new ParallelProcessor {}
  import Shivering._
  def startShivering[T:AnimatedFloat2](tgt:T) {
    if (!started) {

     // log("start shivering"+count)
      val impl = implicitly[AnimatedFloat2[T]]
      val (updater,canceller) = Swinger.update(2,impl.getX(tgt),impl.getY(tgt),tgt)
      val shiver = auto[IntegratingFTask](igPool)
      shiver.setUpdater(updater)
      shiver.setFinalizer(()=>{
       // log("finished")
        canceller()
        shiver.reset()
      })
      shiver.setCanceller(()=>{
       // log("cancelled")
        canceller()
        shiver.reset()
      })
      started = true
      shiverProcessor.add(shiver)
    }
  }
  def stopShivering() {
    if(started){
      shiverProcessor.clearTasks()
      started = false
     // log("stop shivering")
    }
  }

  override def update(delta: Float): Unit = {
    super.update(delta)
    shiverProcessor.update(delta)
  }
}
object Shivering{
  implicit val igPool = Pool[IntegratingFTask](1000)
}