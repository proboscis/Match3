package com.glyph._scala.game.action_puzzle

import com.badlogic.gdx.graphics.Color
import com.glyph._scala.lib.libgdx.actor.Tasking
import com.glyph._scala.lib.libgdx.actor.action.Shivering
import com.glyph._scala.lib.util.{ColorUtil, Logging}
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph._scala.lib.util.reactive.{Varying, Var, Reactor}
import com.badlogic.gdx.utils.{ObjectMap, NumberUtils}
import com.badlogic.gdx.scenes.scene2d.Actor
import com.glyph._scala.lib.util.json.RJSON
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import scala.collection.mutable
import scala.util.{Failure, Success}
import scala.language.implicitConversions
import scala.collection.immutable.HashMap

/**
 * use table as a layout manager! and don't ever let them use drawing method.
 * @author glyph
 */
class Token[T,Tgt<:Actor](var panel: ActionPuzzle[T]#AP, var tgtActor: Tgt)
  extends Table // problem is here!
  with Logging
  with Reactor
  with Tasking
  with Shivering {

  import com.glyph._scala.lib.libgdx.conversion.AnimatingGdx._
  implicit val impl = animatedActor[Tgt]

  def init(p: ActionPuzzle[T]#AP, tgt: Tgt) {
    panel = p
    tgtActor = tgt
    add(tgtActor).fill.expand
  }

  val hashColor = (t: Any) => new Color(NumberUtils.floatToIntColor(t.hashCode()))

  //this should not be done in here!? no, the elements should be given in the constructor!
  override def act(delta: Float) {
    super.act(delta)

    {
      if (panel != null && panel.value != null) {
        import Token._
        val c = colorMap(panel.value)
        tgtActor.setColor(c)
        if (panel.isSwiping() || panel.isFalling()) {
          tgtActor.getColor.set(c).mul(0.7f)
        } else if (panel.isMatching) {
          ColorUtil.ColorToHSV(c).add(0, -0.4f, 0.2f).toColor(tgtActor.getColor)
          startShivering(tgtActor)
        } else if (!panel.isMatching) {
          stopShivering()
        }
        //tgtActor.setColor(hashColor(tgtActor))
      }
    }
  }

  def resetForPool() {
    setColor(Color.WHITE)
    remove()
    clearReaction()
    //clearChildren()
    //setScale(1)
    clear()
    clearActions()
    clearListeners()
    panel = null
    tgtActor = null.asInstanceOf[Tgt]
    stopShivering()
  }

  override def toString: String = "x" -> getX :: "y" -> getY :: "w" -> getWidth :: "h" -> getHeight :: Nil mkString("(", ",", ")")
}

object Token {
  //println("token object")
  import ColorTheme._
  //TODO this map is generating SOME!!!!!
  //val colorMap: Any Map Varying[Color] = Map[Any, Varying[Color]](0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life)
  //println(colorMap.getClass)
  //val colorMap: Any Map Varying[Color] = Map[Any, Varying[Color]](0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life) withDefault (_ => Var(Color.WHITE))
  //val colorMap = mutable.HashMap[Any,Varying[Color]](0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life)
  /*
  val colorMap = new ObjectMap[Any,Varying[Color]] with ( Any => Varying[Color]){
    def apply(v1: Any): Varying[Color] = this.get(v1)
  }
  colorMap.put(0,fire)
  colorMap.put(1,thunder)
  colorMap.put(2,water)
  colorMap.put(3,life)*/
  def colorMap(key:Any):Color = vColorMap().get(key.asInstanceOf[Int])
}

object ColorTheme {
  lazy val scheme = RJSON(GdxFile("constants/colors.js").map {
    _ getOrElse ""
  })
  val varyingColorMap:Varying[String Map Color]= scheme.map(_.asMapTry.map(_.mapValues(_.asOpt[String].map(Color.valueOf).getOrElse(Color.WHITE)))).map{
    case Success(s) => s
    case Failure(e) => e.printStackTrace();Map()
  }.map(_.toSet.toMap)//this is required to avoid the inner usage of MappedValues!
  //ok,eliminated excessive allocation of Some instances.
  //do not ever call "apply" of scala map since it uses pattern match internally and
  //allocates an Option for each call.

  val colorNames = "turquoise"::"peter_river"::"amethyst"::"sun_flower"::"carrot"::"alizarin"::Nil
  val intToColorName = colorNames.zipWithIndex.map{
    case (str,i)=>(i,str)
  }.toMap
  val vColorMap = varyingColorMap.map {
    case map =>
      intToColorName mapValues map
  }.map(map =>{
    val nm =  new com.badlogic.gdx.utils.ObjectMap[Int,Color]()
    for((k,v)<-map){
      nm.put(k,v)
    }
    nm
  })//now every thing is fine!!!
  implicit def json2Str(json: RJSON): Varying[Color] = json.as[String] map {
    str => Color.valueOf(str getOrElse "ffffff")
  }

  type VC = Varying[Color]
  val fire: VC = scheme.fire
  val water: VC = scheme.water
  val thunder: VC = scheme.thunder
  val monster: VC = scheme.monster
  val life: VC = scheme.life
  val move: VC = scheme.move
}