package com.glyph.scala.game.action_puzzle.view

import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.{Table, Skin}
import com.glyph.scala.game.action_puzzle._
import com.glyph.scala.lib.util.updatable.reactive.Eased
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.{SpriteActor, Updating}
import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph.scala.lib.util.reactive.Reactor
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.Screen
import scala.reflect.ClassTag
import com.glyph.scala.lib.libgdx.actor.blend.AdditiveBlend
import com.glyph.scala.game.action_puzzle.screen.{APViewTable, Resource, Trailed, Scoring}


/**
 * @author glyph
 */
class ActionPuzzleTable(implicit assets: AssetManager) extends Table with Reactor with Logging {
  //TODO design the gauge
  //TODO GameOver
  //TODO title screen
  //TODO loading screen

  //TODO what you can do to inject the dependency is to split the procedure into functions,
  //TODO and making a class as trait as much as possible
  //TODO so, do not declare val as much as possible. reusable objects do not have public values

  val skin = assets.get[Skin]("skin/holo/Holo-dark-xhdpi.json")
  val game = new ComboPuzzle
  val resource = new Resource()

  import game._

  val easedScore = Eased(score map (_.toFloat), Interpolation.exp10Out.apply, _ / 10f)
  val view = new APView[Int, SpriteActor](game.puzzle)(APViewTable.textured(resource.roundRect), ClassTag(classOf[SpriteActor]))
    with Scoring[Int, SpriteActor]
    with Trailed[Int, SpriteActor]
    with Updating
    with AdditiveBlend {
    def score: Int = game.score()

    def texture: Texture = resource.particle
  }
  view.add(easedScore)
  val scoreLabel = new RLabel(skin, easedScore.map("%.0f".format(_)))
  scoreLabel.setColor(Color.DARK_GRAY)

  import Actions._
  import Interpolation._

  reactVar(score) {
    val ic = scoreLabel.getColor.cpy()
    var prevAction: Action = null
    s => {
      if (prevAction != null) {
        scoreLabel.removeAction(prevAction)
      }
      prevAction = sequence(color(Color.WHITE, 0.5f, exp10Out), color(ic, 1f, exp10In))
      scoreLabel.addAction(prevAction)
    }
  }
  val comboLabel = new RLabel(skin, combo map (_.toString))
  val inner = new Table()
  inner.debug
  inner.add(scoreLabel).expand
  inner.add(comboLabel).expand
  this.add(inner).fill.expand.row
  val apViewCell = this.add(view).fill().expand()
  apViewCell.left.row

  val gaugeCell = this.add(new Table {
    val back = SpriteActor(new Sprite(assets.get[Texture]("data/dummy.png")))
    add(back).fill.expand
    time map (_ / 60f * getWidth) += back.setWidth
  })
  gaugeCell.fill().expand()
  game.onPanelAdd = view.panelAdd
  game.onPanelRemove = seq => {
    view.panelRemove(seq)
  }


  reactSome(view.swipeChecker) {
    case checker => {
      view.swipeStopper()
      checker(puzzle.pooledSwipe)
      //view.startSwipeCheck(puzzle.pooledSwipe)
    }
  }
  this.debug()
  /*
  init after the layout is setup
   */
  puzzle.initialize()

  override def layout(): Unit = {
    apViewCell.size(getWidth, getWidth)
    gaugeCell.size(getWidth, getWidth / 10)
    super.layout()
  }

  override def act(delta: Float): Unit = {
    game.update(delta)
    super.act(delta)
  }
}

object ActionPuzzleTable {
  val requiredAssets: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/dummy.png", "data/particle.png", "data/sword.png", "data/round_rect.png"),
    classOf[Skin] -> Seq("skin/holo/Holo-dark-xhdpi.json")
  )
}

class ActionPuzzleTableScreen extends ScreenBuilder {
  def requiredAssets = ActionPuzzleTable.requiredAssets

  def create(assetManager: AssetManager): Screen = new ConfiguredScreen {
    val table = new ActionPuzzleTable()(assetManager)
    root.add(table).size(STAGE_WIDTH, STAGE_HEIGHT)
    root.debug()
  }
}