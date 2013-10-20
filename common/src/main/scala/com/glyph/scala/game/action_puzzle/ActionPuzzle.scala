package com.glyph.scala.game.action_puzzle
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
class ActionPuzzle {

  def initialize(){}
}
object ActionPuzzle{
  type Callback[R] = R=>Unit
  type Animation[P,R] = P => Callback[R] => Unit
  def concat[P,R,S](fa:Animation[P,R],fb:Animation[R,S]):Animation[P,S] = (paramA:P) => (cb:Callback[S]) =>{
    fa(paramA){
      resultA =>fb(resultA){
        resultB => cb(resultB)
      }
    }
  }
  def concat2[P,R,S](fa:Animation[P,R],fb:Animation[R,S]):Animation[P,S] = paramA=> callback =>fa(paramA)(fb(_)(callback))
}