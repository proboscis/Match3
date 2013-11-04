package com.glyph.scala.game.action_puzzle

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.util.reactive.Var

/**
 * @author glyph
 */
class ActionPuzzle3 {
  import GMatch3._
  //list up the state of panels!
  val ROW = 6
  val COLUMN = 6
  val fixed:Var[Puzzle[AP]] = Var(Vector())
  val falling:Var[Puzzle[AP]] = Var(Vector())
  val swiping:Var[Puzzle[AP]] = Var(Vector())
  val future:Var[Puzzle[AP]] = Var(Vector())

  def scanRemoveFill(){
    val scanned = fixed().scanAllWithException(3)()
    val (left,fallen) = fixed().remove(scanned.flatten.map{_._1}.distinct)
    fixed() = left
    falling() = fallen append falling()
    future() = fixed() append falling()
  }
  def updateFallingTarget
  def swipe(x:Int,y:Int,nx:Int,ny:Int)
  def fill(){
    val filling = future().createFillingPuzzle(seed,COLUMN)
    falling() = falling() append filling()
    future() = fixed() append falling()//TODO swipingを考慮してfutureを決める?
  }
  def seed:()=>AP = new AP
  def remove(panels:Seq[AP])
  def initialize(){
    fill()
  }
  def update(delta:Float){

  }
}

class APView(assets:AssetManager) extends WidgetGroup
class AP extends GMatch3.Panel{
  val x = Var(0f)
  val y = Var(0f)
  val vx = Var(0f)
  val vy = Var(0f)
}