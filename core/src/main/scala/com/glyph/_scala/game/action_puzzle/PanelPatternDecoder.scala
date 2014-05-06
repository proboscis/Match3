package com.glyph._scala.game.action_puzzle

import com.glyph._scala.lib.event.EventManager
import sun.awt.geom.Crossings
/**
 * @author glyph
 */
class PanelPatternDecoder(events:EventManager) extends (PanelRemove =>Unit){
  override def apply(panels: PanelRemove): Unit = {
    panels.removed match{
      case Nil =>
    }
  }
}
object PanelPattern{

}
class PanelPattern(kind:Int,panels:Seq[ActionPuzzle[_]#AP])
//alright, what kind of information is required for showing the effect?
//that was the most important part.
//how would i show what has happened to the user?
//how do i show...