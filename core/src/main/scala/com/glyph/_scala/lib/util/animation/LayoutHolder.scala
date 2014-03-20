package com.glyph._scala.lib.util.animation

import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph._scala.lib.util.animation.Adapter.{LayoutAnimationConstructor, Layout}
import com.glyph._scala.lib.util.animation.LayoutHolder.AC
import com.glyph._scala.lib.libgdx.actor.ActorOps
import ActorOps._
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
trait LayoutHolder extends Group{
  //add actors in layouts and perform animation
  def in(layout:Layout,animation:AC)
  //remove actors in layouts after performing animation
  def out(layout:Layout,animation:AC)
}
object LayoutHolder{
  //type ailias
  type AC = LayoutAnimationConstructor
}
trait DefaultLayoutHolder extends LayoutHolder
  with Logging{
  private def addActorsInLayout(layout:Layout){
    for(LayoutInfo(_,_,target)<-layout){
      addActor(target)
    }
  }
  //add actors in layouts and perform animation
  def in(layout: Layout, animation: AC): Unit = {
    addActorsInLayout(layout)
    animation(this)(layout).start(()=>{
      log("finished in animation!")
    })
  }
}
