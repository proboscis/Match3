package com.glyph.scala.lib.libgdx.actor.ui

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.glyph.scala.lib.util.reactive.{EventSource, Var, Reactor}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Touchable, Actor}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph.scala.lib.util.json.RJSON
import scala.language.dynamics
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.GdxUtil

/**
 * @author glyph
 */
class SlideView(config: RJSON = SlideView.config) extends WidgetGroup with Reactor {
  type View = Actor
  type Pair = (View, () => Unit)
  type ?[A] = Option[A]
  private val duration = config.duration.as[Float]
  private val interpolation = config.interpolation.as[Interpolation]
  private val viewWidth = config.viewWidth.as[Float]
  private var ins: List[Pair] = Nil
  private var outs: List[Pair] = Nil

  //private def slides: List[Pair] = ins ::: outs

  val shownPress = EventSource[Actor]()

  def shown : ?[View] = showing.map{_._1._1}

  private var showing: ?[(Pair,InputListener)] = None
  setTouchable(Touchable.childrenOnly)

  def slideIn(view: View,outDone: ()=>Unit = ()=>{}, inDone: => Unit = Unit) {
    slideOut()
    in((view,outDone), inDone)
  }

  def slideOut(callback: => Unit = Unit) {
    //println("slide out call:"+ins.size)
    ins foreach {
      out(_, callback)
    }
    showing foreach {
      case (pair@(view,cb),listener)=> {
        out(pair,callback)
        view.removeListener(listener)
      }
    }
    showing = None
  }

  import Actions._

  private def out(pair: Pair, callback: => Unit = Unit) {
    for {
      d <- duration()
      i <- interpolation()
    } {
      val (view, removed) = pair
      //println("out call :"+outs.size)
      view.clearActions()
      val move = moveTo(getWidth, 0)
      move.setDuration(d)
      move.setInterpolation(i)
      view.addAction(sequence(move, run(new Runnable {
        def run() {
          //println("bf run:"+outs.size)
          outs = outs filter {
            case (v,f) => v != view
          }
          removed()
          //println("run:" + outs.size)
          if (outs.size == 0) callback //Callback
        }
      }), Actions.removeActor()))
      view.setTouchable(Touchable.disabled)
      outs = pair :: outs
      ins = ins filter {
        case (v,f) => v != view
      }
    }
  }

  private def in(pair: Pair, callback: => Unit) {
    for {
      w <- viewWidth()
      d <- duration()
      i <- interpolation()
    } {
      val (view, removed) = pair
      view.setSize(getWidth * w, getHeight)
      view.setPosition(getWidth, 0)
      view.setTouchable(Touchable.enabled)
      val move = moveTo(getWidth - view.getWidth, 0)
      move.setDuration(d)
      move.setInterpolation(i)
      val il = new InputListener{
        override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = true
        override def touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
          super.touchUp(event, x, y, pointer, button)
          //TODO fix this issue that the event is not emitted properly
          shownPress.emit(view)
        }
      }
      view.addAction(sequence(move, run(new Runnable {
        def run() {
          //println("in complete")
          ins = ins filter {
            case (v,f) => v != view
          }
          for(((prev,cb),listener) <- showing){prev.removeListener(listener)}
          showing = Some(pair,il)
          view.addListener(il)
          callback
        }
      })))
      GdxUtil.post {
        addActor(view)
        view.toFront()
        ins = pair :: ins
        //println("add in:"+ins.size)
        //println("num of slides:"+slides.size)
      }
    }
  }
}

object SlideView {
  val config = RJSON(Var(
    """
      |config = {
      |  viewWidth:0.6,
      |  duration:0.3,
      |  interpolation:Packages.com.badlogic.gdx.math.Interpolation.exp10Out
      |}
    """.stripMargin))
}
