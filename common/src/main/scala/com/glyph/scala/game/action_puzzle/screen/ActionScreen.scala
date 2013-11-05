package com.glyph.scala.game.action_puzzle.screen

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.libgdx.actor.Scissor
import com.glyph.scala.lib.util.reactive.Reactor
import scalaz._
import Scalaz._
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.action_puzzle.{APView, ActionPuzzle3, ActionPuzzle2}
import com.glyph.scala.game.action_puzzle.view.Paneled
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Label}
import com.glyph.scala.lib.util.{reactive, Logging}
import com.glyph.scala.lib.libgdx.GdxUtil

/**
 * @author glyph
 */
class ActionScreen(assets: AssetManager) extends TabledScreen with Reactor with Logging {
  val constants = RVJSON(GdxFile("constants/string.js"))
  val colors = RVJSON(GdxFile("constants/colors.js"))

  //RVJSON(constants.colors.asVnel[String])
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))

  //TODO ControllerはViewのイベントをModelに渡すためのもの。
  //TODO ビューの状態遷移はビューで、ゲームの状態（ターン等）はモデルクラスでやればよい。

  reactVar(colors.background.as[String].map {
    opt => opt.map(Color.valueOf) | Color.WHITE
  }) {
    color => backgroundColor = color
  }
  val skin = assets.get[Skin]("skin/default.json")
  val puzzle = new ActionPuzzle3
  val view = new APView(puzzle,assets)
  /*
  val puzzle = new ActionPuzzle2
  class Token(val panel: puzzle.P) extends Label(panel.n + "", skin) with Reactor

  //  class Token(panel:puzzle.P) extends SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png"))) with Reactor{
  //    setColor(40*panel.n,40*panel.n,40*panel.n,40*panel.n)
  //  }
  val view = new Paneled[Token] with Scissor {
    def row: Int = puzzle.SIZE
    def column: Int = puzzle.SIZE
    puzzle.startSwipeCheck = startSwipeCheck
    puzzle.stopSwipeCheck = stopSwipeCheck
    puzzle.addPanel = (p, x, y) => {
      val token = new Token(p)
      token.setSize(panelW, panelH)
      log(panelW, panelH)
      token.reactVar(p.x) {
        x => {
          //log("width"+getWidth)
          //log("divX"+divX)
          //log("x:"+calcPanelX(x))
          token.setX(calcPanelX(x))
        }
      }
      token.reactVar(p.y) {
        y => {
          //log("y:"+calcPanelX(y))
          token.setY(calcPanelY(y))
        }
      }
      GdxUtil.post {
        puzzleGroup.addActor(token)
      }
      tokens += token
    }
    puzzle.removePanel = (panel) =>{
      for(t <- tokens.find(_.panel == panel)){
        t.remove()
        tokens -= t
      }
    }
  }
  //  val token = new SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
  //  token.setSize(100,100)
  //  view.puzzleGroup.addActor(token)

  //val view = new Box2DBed(puzzle.world)
  //view.setSize(STAGE_WIDTH,STAGE_HEIGHT)
  /*
   init layout
   */
   * */

  root.add(view).fill().expand()
  root.invalidate()
  root.layout()

  import reactive._
  import puzzle._
  reactVar(fixed~falling~future){
    case a~b~c=> "====================="::(a::b::c::Nil map(_.text)) foreach log
  }
  puzzle.panelAdd = view.panelAdd
  puzzle.panelRemove = view.panelRemove
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def render(delta: Float): Unit = {
    super.render(delta)
    puzzle.update(delta)
  }
}
