package com.glyph.scala.game.action_puzzle

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Label, Skin, WidgetGroup}
import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import com.glyph.scala.game.action_puzzle.GMatch3.Panel
import scalaz._
import Scalaz._
import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.lib.util.updatable.task.ParallelProcessor
import com.glyph.scala.game.action_puzzle.view.Paneled
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.libgdx.actor.ui.RLabel

/**
 * @author glyph
 */
class ActionPuzzle3 extends Reactor{
  import GMatch3._
  //list up the state of panels!
  val ROW = 6
  val COLUMN = 6
  val gravity = -10f
  val processor = new ParallelProcessor{}
  def initializer:Var[Puzzle[AP]]= Var(Vector(0 until ROW map{_=>Vector()}:_*))
  val fixed = initializer
  val falling= initializer
  val swiping= initializer// swiping < fixed
  val future= initializer
  //callbacks
  var panelAdd = (panels:Seq[(AP,Int,Int)]) => {}
  var panelRemove = (panels:Seq[AP])=>{}
  //util functions
  def scanAll = scanAllWithException(fixed())(3)(swiping().contains)
  def scanAllDistinct = scanAll.flatten.map{_._1}.distinct
  def scanRemoveFill(){
    println(scanAll)
    remove(scanAllDistinct)
    fill()
  }
  def swipe(x:Int,y:Int,nx:Int,ny:Int) = ???
  def fill(){
    val filling = future() createFillingPuzzle(seed,COLUMN)
    if(filling.exists(!_.isEmpty)){
      falling() = falling() append filling
      future() = fixed() append falling()
      val indexed = filling.flatten.map{p => val (x,y) = filling.indexOfPanelUnhandled(p);(p,x,y)}
      val futureIndexed = filling.flatten.map{
        p => val(x,y) = future().indexOfPanelUnhandled(p);(p,x,y)
      }
      for((p,x,y) <- indexed){
        p.x() = x
        p.y() = COLUMN + y
      }
      futureIndexed |> panelAdd
    }
    updateTargetPosition()
  }
  def seed:()=>AP=()=>MathUtils.random(0,1) |> (new AP(_))
  def remove(panels:Seq[AP]){
    if(!panels.isEmpty){
      val (left,fallen) = fixed().remove(panels)
      panels |> panelRemove
      fixed() = left
      falling() = fallen append falling()
      future() = fixed() append falling()
    }
    updateTargetPosition()
  }
  def updateTargetPosition(){
    for(row <- falling();p<-row){
      for((tx,ty)<- future().indexOfPanelOpt(p)){
        p.tx() = tx
        p.ty() = ty
      }
    }
  }
  def initialize(){
    fill()
  }
  def update(delta:Float){
    val (finished,continued) = falling().unzip{
      _.partition(p=>{
        p.vy() += gravity*delta
        p.update(delta)
      })
    }
    //println("continued:"+continued.text)
    if(finished.exists(!_.isEmpty)){
      println("finished:"+finished.text)
      falling() = continued
      for(row <- finished;p <- row){
        fixed() = fixed().updated(p.tx(),fixed()(p.tx()):+p)
      }
      scanRemoveFill()
    }
  }
  class AP(val n:Int) extends GMatch3.Panel{
    val x = Var(0f)
    val y = Var(0f)
    val vx = Var(0f)
    val vy = Var(0f)
    val tx = Var(0)
    val ty = Var(0)
    def matchTo(panel: Panel): Boolean = panel match{
      case p:AP => n == p.n
      case _ => false
    }
    def update(delta:Float):Boolean = {
      val nx = x()+ vx()*delta
      var ny = y()+ vy()*delta
      //TODO make sure not to fall faster than the next panels
      //if about to cross another panel

      val next = PartialFunction.condOpt(ty()-1)(future()(tx()))
      val finished = next match{
        case Some(p) if fixed().exists(_.contains(p)) =>(ny - ty()) < 0f
        case Some(p) if ny - p.y() < 1f => {//if above the next panel
          ny = p.y() + 1
          vy() = p.vy()
          //clear()
          false
        }
        case _ => (ny - ty()) < 0f
      }
      if(finished){
        ny = ty()
        clear()
      }
      x() = nx
      y() = ny
      //println(y(),vy())
      finished
    }
    def clear(){
      vx() = 0
      vy() = 0
    }
    override def toString: String = n + ""
  }
}

class APView(puzzle:ActionPuzzle3,assets:AssetManager) extends WidgetGroup with Paneled[Token] with Reactor with Logging{
  def row: Int = puzzle.ROW
  def column: Int = puzzle.COLUMN
  val skin = assets.get[Skin]("skin/default.json")
  val panelAdd = (added:Seq[(ActionPuzzle3#AP,Int,Int)])=>{
    for((p,x,y) <- added){
      val token = new Token(p,skin){
        override def act(delta: Float){
          super.act(delta)
          setX(calcPanelX(p.x()))
          setY(calcPanelY(p.y()))
        }
      }
      token.setSize(panelW,panelH)
      //token.reactVar(p.x)(calcPanelX(_) |> token.setX)
      //token.reactVar(p.y)(calcPanelY(_) |> token.setY)
      //token.reactVar(p.y)(log)
      tokens += token
      puzzleGroup.addActor(token)
    }
  }
  val panelRemove = (removed:Seq[ActionPuzzle3#AP]) => {
    for(panel <- removed){
      for(token <- tokens.find(_.panel == panel)){
        tokens -= token
        token.remove()
      }
    }
  }
}
class Token(val panel:ActionPuzzle3#AP,skin:Skin)extends Table with Reactor{
  val rabel = new RLabel(skin,panel.ty.map("%d".format(_)))
  val label = new Label(panel.n+"",skin)
  rabel::label::Nil foreach{_.setFontScale(0.7f)}
  add(label).fill.expand(1,5)//.row()
  //add(rabel).fill.expand(1,1)
}

