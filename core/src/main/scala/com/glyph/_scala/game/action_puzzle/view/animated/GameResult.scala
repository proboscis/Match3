package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui._
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.{Updating, AnimatedTable}
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.glyph._scala.lib.util.reactive.{Reactor, Var}
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.{Drawable, ChangeListener}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.esotericsoftware.tablelayout.{Value, BaseTableLayout}
import com.glyph._scala.game.action_puzzle.ComboPuzzle
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.lib.util.Animated
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import scalaz._
import Scalaz._
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.lib.libgdx.drawable.DrawableCopy
import com.badlogic.gdx.utils.Scaling
import com.glyph._scala.lib.util.layout.GridLayout.Cell
import GameResult._
import com.glyph._scala.lib.libgdx.skin.FlatSkin
import com.glyph._scala.social.SocialManager
import com.glyph._scala.lib.libgdx.actor.widgets.Center
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.glyph._scala.game.action_puzzle.view.animated.TableValueOps.ValueOps

/**
 * @author glyph
 */
class GameResult(style: Style)(implicit processor: ParallelProcessor) extends AnimatedConstructor {
  override def apply(info: AnimatedManager.Info): (AnimatedManager.Callbacks) => Actor with Animated = callbacks => new AnimatedTable
    with Updating
    with Reactor {

    import style._

    val emphasisFont = FontUtil.internalFont("font/code_light.otf", 300)
    emphasisFont.getData.descent = 0
    val labelFont = FontUtil.internalFont("font/corbert.ttf", 100)
    val buttonFont = FontUtil.internalFont("font/corbert.ttf", 50)
    buttonFont.getData.descent = 0
    val wetAsphalt: Color = "wet_asphalt"
    val clouds: Color = "clouds"
    val descStyle = new LabelStyle(labelFont, clouds)
    val emphasisStyle = new LabelStyle(emphasisFont, clouds)
    val buttonLabelStyle = new LabelStyle(buttonFont, wetAsphalt)

    import FlatSkin._

    implicit def str2SkinColor[T <: String](str: T): Color = skin.getColor(str)

    val exit = new TintedButton("data/noun/exit.png", wetAsphalt) with Change
    val replay = new TintedButton("data/noun/refresh.png", wetAsphalt) with Change
    val podium = new TintedButton("data/noun/podium.png", wetAsphalt) with Change
    exit.onChange = (e, a) => callbacks("title")(Map())
    replay.onChange = (e, a) => callbacks("replay")(Map())
    podium.onChange = (e, a) => SocialManager.manager.showGlobalHighScore()
    val exitTable = new Table
    exitTable.debug()
    exitTable.add(exit).fill.expand.bottom.row
    exitTable.add(Center(new Label("exit", buttonLabelStyle))).fill
    val podiumTable = new Table
    val replayTable = new Table
    podiumTable.add(podium).fill.expand.bottom.row
    podiumTable.add(Center(new Label("ranking", buttonLabelStyle))).fill
    replayTable.add(replay).fill.expand.bottom.row
    replayTable.add(Center(new Label("replay", buttonLabelStyle))).fill
    setBackground(skin.getDrawable("peter_river"))
    debug(BaseTableLayout.Debug.all)
    log("creating game result view")
    val score = info.lift("score").getOrElse(0).asInstanceOf[Int]
    log("score", score)
    val shownScore = Var(0f)
    val ease = Eased(shownScore, Interpolation.exp10Out.apply, t => 2f)
    shownScore := score
    addUpdatable(ease)
    val scoreLabel = new Label("Score", descStyle)
    val heatLabel = new Label("Heat", descStyle)
    val heatValueLabel = new Label("Alot", descStyle)
    val scoreValueLabel = new RLabel(skin, ease.map(s => "%.0f".format(s))) {
      setStyle(new LabelStyle(skin.get(classOf[LabelStyle])) <| (_.font = labelFont))
    }
    val resultLabel = new Label("RESULT", emphasisStyle)
    err("textBounds", resultLabel.getTextBounds.height, resultLabel.getHeight, resultLabel.getPrefHeight)
    val scoreTable = new Table
    val menuTable = new Table
    scoreTable.debug()
    menuTable.debug()
    scoreTable.pad(padY, padX, padY, padX).top()
    scoreTable.defaults().space(marginY, marginX, marginY, marginX)
    scoreTable.add(resultLabel).colspan(2).row()
    scoreTable.add(scoreLabel).left
    scoreTable.add(scoreValueLabel).right.row()
    scoreTable.add(heatLabel).left
    scoreTable.add(heatValueLabel).right
    menuTable.setBackground(skin.getDrawable("clouds"))
    menuTable.defaults.fill.expand.pad(padY, padX, padY, padX).space(marginY, marginX, marginY, marginX)
    menuTable.add(replayTable).width(1f.width).padLeft(padX)
    menuTable.add(podiumTable).width(1f.width)
    menuTable.add(exitTable).width(1f.width).padRight(padX)
    add(scoreTable).height((1.309f / 1.618f).height).fill.expand.row
    add(menuTable).height((0.309f / 1.618f).height).fill.expandX
  }
}

import FlatSkin._

class TintedButton(drawable: Drawable, color: Color) extends Imaged(tint(drawable, color), tint(drawable, lighter(color)))

class Imaged(up: Drawable, down: Drawable) extends Button(new ButtonStyle) {
  val image = new Image(up)
  image.setScaling(Scaling.fit)
  add(image).fill.expand

  private def updateImage() {
    if (isPressed) {
      image.setDrawable(down)
    } else {
      image.setDrawable(up)
    }
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    updateImage()
    super.draw(batch, parentAlpha)
  }
}

object GameResult {



  case class Style(padX: Float = 20, padY: Float = 20, marginX: Float = 20, marginY: Float = 20, skin: Skin)

}

trait TableValueOps{
  implicit def floatToValueOps(percent:Float):ValueOps = new ValueOps(percent)
}
object TableValueOps extends TableValueOps{
  implicit class ValueOps(val percent: Float) extends AnyVal {
    def height = Value.percentHeight(percent)
    def width = Value.percentWidth(percent)
  }
}

trait Change {
  self: Actor =>
  var onChange = (event: ChangeEvent, actor: Actor) => {}
  self.addListener(new ChangeListener {
    def changed(event: ChangeEvent, actor: Actor) {
      onChange(event, actor)
    }
  })
}




