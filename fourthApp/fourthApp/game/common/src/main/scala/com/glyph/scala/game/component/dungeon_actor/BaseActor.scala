package com.glyph.scala.game.component.dungeon_actor

import com.glyph.scala.game.component.dungeon_actor.DungeonActor.Direction
import com.glyph.scala.game.GameConstants
import com.glyph.scala.game.dungeon.animation.Animation

/**
 * @author glyph
 */
trait BaseActor extends DungeonActor{
  self =>
  import Direction._
  //val log = Glyph.log("BaseActor")_

  var state :State = Idle

  override def onMovePhase() {
    log("onMovePhase")
    super.onMovePhase()
    tryMove(Direction.RIGHT)
  }

  override def tryMove(dir: Direction) {
    state.tryMove(dir)
    super.tryMove(dir)
  }


  override def doAction() {
    super.doAction()
  }

  trait State{
    def tryMove(dir:Direction){}
  }
  object Moving extends State{}
  object Idle extends State{
    override def tryMove(dir: Direction.Direction) {
      log("tryMove")
      //TODO if this is called directory, it is required to call the animation manager to start animation of the others
      val animation = new Animation(dungeon.animationManager){
        val duration = 0.3f
        val speed = GameConstants.CELL_WIDTH/duration
        var timer = duration
        def update(delta: Float) {
          timer -= delta
          dir match {
            case Direction.RIGHT => transform.position.x +=speed * delta
            case Direction.LEFT => transform.position.x -= speed * delta
          }
          if (timer < 0){
            end()
          }
        }
      }
      state = Moving
      animation.onAnimationEnd+={()=>{state = Idle}}
      dungeon.animationManager.postAnimation(animation)
    }
  }
}
