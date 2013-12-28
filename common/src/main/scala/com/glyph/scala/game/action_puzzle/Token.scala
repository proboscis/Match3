package com.glyph.scala.game.action_puzzle

import com.glyph.scala.game.puzzle.view.match3.ColorTheme
import com.badlogic.gdx.graphics.{Texture, Color}
import com.glyph.scala.lib.libgdx.actor.{Tasking, SpriteActor}
import com.glyph.scala.lib.libgdx.actor.action.Shivering
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.lib.util.{ColorUtil, reactive, Logging}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{WidgetGroup, Skin, Table}
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.glyph.scala.lib.util.reactive.{Varying, Var, Reactor}
import com.badlogic.gdx.utils.NumberUtils

/**
 * @author glyph
 */
class Token[T](var panel: ActionPuzzle[T]#AP, assets: AssetManager)
  extends Table // problem is here!
  with Logging
  with Reactor
  with Tasking
  with Shivering {
  debug()


  val sprite = new Sprite(assets.get[Texture]("data/round_rect.png"))
  val skin = assets.get[Skin]("skin/holo/Holo-dark-xhdpi.json")
  val spriteActor = new SpriteActor
  spriteActor.sprite.set(sprite)
  import com.glyph.scala.lib.libgdx.conversion.AnimatingGdx._

  def init(p: ActionPuzzle[T]#AP) {
    panel = p
    import Token._
    val c = (colorMap.get(panel.value) | Var(Color.WHITE)) ~ panel.isSwiping ~ panel.isFalling ~ panel.isMatching map {
      case col ~ swiping ~ falling ~ matching => (swiping | falling) ? col.cpy().mul(0.7f) | (matching ? {
        val hsv = ColorUtil.ColorToHSV(col)
        hsv.v += 0.2f
        hsv.s -= 0.4f
        hsv.toColor
      } | col)
    }
    add(spriteActor).fill.expand
    //spriteActor.setSize(50,50)
    /*
    val group = new WidgetGroup
    val label = new RLabel(skin,(panel.tx ~ panel.ty).map{
      case x~y => "%.1f,%.1f".format(x,y)
    })
    label.setFontScale(0.5f)
    */
    /*
    group.addActor(label)
    group.addActor(spriteActor)
    //add(group).size(getWidth/2,getHeight)
    add(group).fill.expand
    */
    reactVar(panel.isMatching) {
      flag => if (flag) startShivering(spriteActor) else stopShivering()
    }
    //reactVar(c)(spriteActor.setColor)
    /*
    reactSome(p.next){
      case n => {
        val c = new Color(NumberUtils.floatToIntColor(n.hashCode))
        spriteActor.setColor(c)
      }
    }
    */
    reactVar(c)(spriteActor.setColor)
  }

  def resetForPool() {
    setColor(Color.WHITE)
    remove()
    clearReaction()
    clearChildren()
    //setScale(1)
    //clear()
    clearActions()
    clearListeners()
  }

  override def toString: String = "x" -> getX :: "y" -> getY :: "w" -> getWidth :: "h" -> getHeight :: Nil mkString("(", ",", ")")
}

object Token {

  import ColorTheme._

  val colorMap: Any Map Varying[Color] = Map(0 -> ColorTheme.fire, 1 -> thunder, 2 -> water, 3 -> life)
}