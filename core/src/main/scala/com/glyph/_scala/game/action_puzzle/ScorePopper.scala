package com.glyph._scala.game.action_puzzle

import com.badlogic.gdx.scenes.scene2d.Group
import com.glyph._scala.lib.libgdx.actor.SpriteBatchRenderer
import aurelienribon.tweenengine.{Tween, TweenManager}
import com.glyph._scala.lib.util.pool.Pool
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite}
import com.glyph._scala.lib.libgdx.WordParticle
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.font.FontUtil
import FontUtil._
import com.glyph._scala.lib.util.Logging

/**
 * @author glyph
 */
class ScorePopper(font:BitmapFont) extends Group with SpriteBatchRenderer with Logging{
  implicit val renderer = this
  implicit val manager = new TweenManager
  implicit val spritePool = Pool[Sprite](10000)
  private val widthFolder = (f: Float, s: Sprite) => f + s.getWidth
  private val heightFolder = (f: Float, s: Sprite) => f + s.getHeight
  Tween.registerAccessor(classOf[Sprite], SpriteAccessor)
  Tween.setCombinedAttributesLimit(4)
  log("set tween:combinedAttributesLimit(4)")
  def halfW(seq: Seq[Sprite]) = (0f /: seq)(widthFolder) / 2f

  def meanH(seq: Seq[Sprite]) = (0f /: seq)(heightFolder) / seq.size

  def showScoreParticle(x:Float,y:Float,height:Float,score: Int) {
    val sprites = WordParticle.StringToSprites(font)(score.toString)(0.7f)
    WordParticle.start(sprites, WordParticle.popSprites(sprites)(x - halfW(sprites), y, () => height-meanH(sprites), 0.7f))
    log("show score")//TODO this is not called
  }

  override def act(delta: Float): Unit = {
    super.act(delta)
    manager.update(delta)
  }
}
