package com.glyph.scala.game.action_puzzle

/**
 * @author glyph
 */
class Match3Scanner[T](W:Int,H:Int){
  def scanAll(puzzle:IndexedSeq[IndexedSeq[T]])(filter:(T,T)=>Boolean)(processor:IndexedSeq[T]=>Unit){
    //procedural puzzle scanner
  }
}
