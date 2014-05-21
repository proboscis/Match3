package com.glyph._scala.game.action_puzzle

import com.glyph._scala.lib.event.EventManager
import sun.awt.geom.Crossings
import com.badlogic.gdx.math.{Vector2, Matrix3}
import scalaz.Scalaz
import com.glyph.ClassMacro._
/**
 * @author glyph
 */
class PanelPatternDecoder(events:EventManager){
  import PanelPattern._
  events += ((e:PanelRemove)=>{
    val positions = e.removed.map(ap => (ap.tx,ap.ty))
    for{
      (x,y)<-positions
      pattern<-distinctPatterns
    }{
      //TODO Lだけ反応しない
      //TODO eliminate allocation
      //events << pattern.toString
      if(pattern.map(p=>(p._1+x,p._2+y)).forall(positions.contains)) {
        events << PanelPattern(patternNames(pattern),e.removed)
      }
    }
  })
}
case class PanelPattern(kind:String,panels:Seq[ActionPuzzle[_]#AP])
object PanelPattern{
  implicit def intPairIsVector2(p:(Int,Int)):Vector2 = new Vector2(p._1,p._2)
  implicit class Vector2IsIntPair(v:Vector2){
    def toIntPair = (Math.round(v.x),Math.round(v.y))
  }
  def displayPattern(pattern:Seq[(Int,Int)]):String ={
    val w = 8
    val buffer = Array.fill(w*w)('.')
    for((x,y)<-pattern){
      buffer((y+w/2)+(x+w/2)*w) = '@'
    }
    buffer.grouped(w).map(_.mkString).mkString("\n")
  }
  val CROSS = "CROSS"->Set((0,0),(1,0),(-1,0),(0,1),(0,-1))
  val T = "T"->Set((-1,-1),(0,-1),(1,-1),(0,0),(0,1))
  val L = "L"->Set((0,-1),(0,0),(0,1),(1,1),(2,1))
  val FIVE = "FIVE"->Set((-2,0),(-1,0),(0,0),(1,0),(2,0))
  val rotations = 0f::90f::180f::270f::Nil
  val patterns = (CROSS::T::FIVE::L::Nil).flatMap{
    case (name,pattern) =>rotations.map{
      deg => s"$name-%.0f".format(deg)->pattern.map(p=>p.rotate(deg).toIntPair).toArray
    }
  }
  val distinctPatterns = patterns.map(_._2.toSet).distinct.map(set=>patterns.find{
    case(name,pat)=>pat.toSet == set
  }.get._2)
  val distinctPatternsWithName = distinctPatterns.map{
    pattern=>patterns.find{
      case(name,pat) => pattern.toSet == pat.toSet
    }.get//None.get since the array is not identical
  }
  val patternNames = distinctPatternsWithName.map(_.swap).toMap
  def main(args: Array[String]) {
    import Scalaz._
    distinctPatternsWithName.map{
      case(name,pat)=>name+"\n"+displayPattern(pat)
    }.foreach(println)
    println(distinctPatterns.size)
    //println(patternNames(T._2))
  }
}
