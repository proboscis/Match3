package com.glyph.scala.game.component.dungeon_actor

import com.glyph.scala.game.dungeon.TurnManager
import com.glyph.scala.game.component.dungeon_actor.DungeonActor.Direction
import com.glyph.scala.game.component.update.Update
import com.glyph.scala.game.GameConstants
import collection.generic.IdleSignalling

/**
 * @author glyph
 */
trait BaseActor extends DungeonActor{
  self =>
  import Direction._
  var state = new Idle

  /**
   * update callback
   */
  owner += ((e:Update)=>{state.update(e.delta)})

  override def onMovePhase() {
    super.onMovePhase()
    //TODO: post animation
  }
  override def onActionPhase() {
    super.onActionPhase()
  }
  def tryMove(dir: Direction) {
    state.tryMove(dir)
    //TODO if this is called directory, it is required to call the animation manager to start animation of the others
  }

  class Idle{
    def tryMove(dir:Direction){
      val next = dir match{
        case RIGHT => getPosition() + 1
        case LEFT => getPosition() -1
      }
      if (dungeon.tryMove(self,next)){
        dTransform.position = next
        state = new Move(dir)
      }
    }
    def update(delta:Float){

    }
  }

  class Move(dir:Direction) extends Idle{
    val duration = 0.3f
    var timer = duration
    val speed = GameConstants.CELL_WIDTH/duration
    override def update(delta: Float) {
      super.update(delta)
      val diff = dir match{
        case RIGHT => speed * delta
        case LEFT => speed * -delta
        case _ => 0
      }
      transform.position.x += diff
      timer -= delta
      if (timer <= 0){
        state = new Idle
      }
    }

    override def tryMove(dir: Direction.Direction) {
      //do nothing
    }
  }
}
