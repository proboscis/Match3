package com.glyph._scala.game.action_puzzle.view.animated

import com.badlogic.gdx.scenes.scene2d.ui._
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager.AnimatedConstructor
import com.glyph._scala.lib.libgdx.actor.{Updating, AnimatedTable}
import com.glyph._scala.lib.util.updatable.reactive.Eased
import com.glyph._scala.lib.util.reactive.{Reactor, Var}
import com.badlogic.gdx.math.Interpolation
import com.glyph._scala.lib.libgdx.actor.ui.RLabel
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.{TextureRegionDrawable, Drawable, ChangeListener}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.esotericsoftware.tablelayout.{Value, BaseTableLayout}
import com.glyph._scala.social.SocialManager
import com.glyph._scala.game.action_puzzle.{ColorTheme, ComboPuzzle, LocalLeaderBoard}
import com.glyph._scala.lib.libgdx.actor.transition.AnimatedManager
import com.glyph._scala.lib.util.Animated
import GameResult._
import com.glyph._scala.lib.util.updatable.task.ParallelProcessor
import com.badlogic.gdx.graphics.{Texture, Color}
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import scalaz._
import Scalaz._
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.font.FontUtil
import com.glyph._scala.game.action_puzzle.view.animated.GameResult.Style
import com.glyph._scala.lib.libgdx.actor.widgets.Center
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.Gdx
import com.glyph._scala.lib.libgdx.drawable.{Tint, DrawableCopy}
import com.badlogic.gdx.utils.Scaling

/**
 * @author glyph
 */
class GameResult(style: Style)(implicit processor: ParallelProcessor) extends AnimatedConstructor {
  override def apply(info: AnimatedManager.Info): (AnimatedManager.Callbacks) => Actor with Animated = callbacks => new AnimatedTable
    with Updating
    with Reactor {
    val emphasisFont = FontUtil.internalFont("font/code_light.otf", 300)
    val labelFont = FontUtil.internalFont("font/corbert.ttf", 100)
    val tinted = (texture:String)=>new DrawableCopy(new Texture(Gdx.files.internal(texture))) with Tint {
      color.set(Color.GRAY)
    }
    val sky = tinted("data/sky.jpg")
    val capture = tinted("data/capture.png")
    //underlying operation is
    //new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("fileName"))))
    val next : Image = "data/icons/next.png"
    val prev : Image = "data/icons/previous.png"
    val plus : Image = "data/icons/add.png"
    next::prev::plus::Nil foreach (_.setScaling(Scaling.fillX))
    import style._

    setBackground(skin.getDrawable("peter_river"))
    //setBackground(sky)
    //capture |> setBackground
    debug(BaseTableLayout.Debug.all)
    log("creating game result view")
    val score = info.lift("score").getOrElse(0).asInstanceOf[Int]
    log("score", score)
    val shownScore = Var(0f)
    val ease = Eased(shownScore, Interpolation.exp10Out.apply, t => 2f)
    shownScore := score
    val scoreLabel = new Label("Score:", new LabelStyle(labelFont, Color.WHITE))
    val scoreValueLabel = Center(new RLabel(skin, ease.map(s => "%.0f".format(s))) {
      setStyle(new LabelStyle(skin.get(classOf[LabelStyle])) <| (_.font = labelFont))
    }).right
    val buttonStyle = new TextButtonStyle() <| (s => {
      s.font = labelFont
      s.fontColor = ColorTheme.varyingColorMap()("peter_river")
      s.fontColor = Color.BLACK
      s.up = skin.getDrawable("white_transparent")
    })
    val replayButton = new TextButton("Replay", buttonStyle) with Change
    val titleButton = new TextButton("Back to Title", buttonStyle) with Change
    val dashBoardButton = new TextButton("Dash Board", buttonStyle) with Change
    replayButton.onChange = (e, a) => callbacks("replay")(Map())
    titleButton.onChange = (e, a) => callbacks("title")(Map())
    dashBoardButton.onChange = (e, a) => SocialManager.manager.showGlobalHighScore()
    add(ease)
    shownScore() = score
    defaults().fill.expandX.height(160)
    setSkin(skin)
    val scoreTable = new AnimatedTable()
    scoreTable.add(Center(new Label("RESULT", new LabelStyle(emphasisFont, Color.WHITE)))).height(300).colspan(2).row()
    scoreTable.add(scoreLabel).fill.expand
    scoreTable.add(scoreValueLabel).fill.expand
    add(scoreTable).height((1f / 1.618f).height).pad(style.pad).row()

    val menuTable = new AnimatedTable() {
      //defaults().space(style.space).pad(0,style.space,0,style.space).fill().expand()
      defaults().pad(40).fill().expand()
      //setBackground(skin.getDrawable("black_transparent"))
      add(prev)
      add(plus)
      add(next)
      /*
      add(replayButton).row()
      add(dashBoardButton).row()
      add(titleButton).row()
      */
    }
    add(menuTable).height((0.618f / 1.618f).height).fill.expand
    log("highscore:==============>")
    LocalLeaderBoard.load(ComboPuzzle.LOCAL_LEADERBOARD).foreach(log)
    log("highscore:<==============")
  }
}

object GameResult {

  implicit class ValueOps(val percent: Float) extends AnyVal {
    def height = Value.percentHeight(percent)

    def width = Value.percentWidth(percent)
  }

  case class Style(pad: Float = 20, space: Float = 20, skin: Skin)

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




