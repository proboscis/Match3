package com.glyph.scala.game.action_puzzle
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{Skin, WidgetGroup}
import com.glyph.scala.game.action_puzzle.view.Paneled
import com.glyph.scala.lib.util.reactive.{Varying, Reactor}
import com.glyph.scala.lib.libgdx.actor._
import com.badlogic.gdx.graphics.{Texture, GL10}
import com.glyph.scala.lib.util.{ColorUtil, Logging}
import com.glyph.scala.lib.util.pool.Pool
import com.badlogic.gdx.graphics.g2d.Sprite
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.updatable.task.{InterpolatedFunctionTask, TimedFunctionTask}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph.scala.lib.util.animator.Explosion
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import com.badlogic.gdx.math.{Interpolation, MathUtils}
import com.glyph.scala.lib.util.pooling_task.ReflectedPooling

/**
 * @author glyph
 */
class APView[T](score:Varying[Int],puzzle: ActionPuzzle[T], assets: AssetManager)
  extends Paneled
  with Reactor
  with Logging
  with Tasking
  with SpriteBatchRenderer
  with AdditiveBlend
  with Scissor {
  /**
   * 新たな力を得るゲーム:cookies! diablo! 対戦ゲーム パズドラ
   * プレイヤーの感情を動かす：
   * 欲求を満たす
   * 新しい世界からの刺激
   * 悲しみからの開放？
   */

  /**
   * つまり、新たな力を得るのが楽しいのだと言える
   */

  /**
   * ゲームの目的は何か？
   * パネルを爽快に消していくことである。
   * パネルを消すと、新たな力を得て更にパネルを消すことが可能となる！
   * しかし時間でそれが失われるようにすればいい。
   *
   * 更に多くのパネルを消すにはどうしたら良いのか？
   * 最初は３つ揃えて３つまで消すことができる
   * 連鎖すると、３＋連鎖数まで消すことが可能となる
   * １０連鎖でマッチ対象が増える
   * ２０連鎖で待ち受け時間が伸びる
   * １００連鎖で自動マッチング機能がつく等？
   *
   * 時間が早くなるってのでどうよ？
   *
   * 面白さは何か？
   * プレイヤーが新しい能力を得、困難を乗り越えることである
   * 困難が無いゲームは面白く無い、となると
   * まずは困難を設定しなければならず、パネルを爽快に消す、
   * というのはその困難を乗り越える楽しみの副産物でしかない。
   * パネルを消す　==　困難の解消であれば良い？
   * パネルを消すことが困難であれば良い。
   * ３つ揃えて消すだけでは困難とは言えない
   * 何か他の制限が必要
   * 制限の候補：
   * 時間制限：
   *  時間経過によるタイマーの減少
   *  時間経過によるモンスターの攻撃、HPの減少
   * 揃え方の制限：
   *  ５つ揃える必要がある？
   *   指定された順序で揃える？
   *
   * ゲームにするにはどうしたらいいのか？
   * ゲームオーバーがなければゲームにならない？
   *
   */

  import Pool._
  import Actions._
  import MathUtils._

  def row: Int = puzzle.ROW

  def column: Int = puzzle.COLUMN

  import com.glyph.scala.lib.libgdx.conversion.AnimatingGdx._
  import SpriteBatchRenderer._
  import com.glyph.scala.lib.libgdx.poolable.PoolingGdx._
  import SBDrawableGdx._
  import ReflectedPooling._

  implicit val spritePool = Pool[Sprite](10000)
  implicit val tokenPool = Pool[Token[T]](() => new Token[T](null, assets), (tgt: Token[T]) => tgt.resetForPool(), row * column * 2)
  implicit val bufPool = Pool[ArrayBuffer[Sprite]](() => ArrayBuffer[Sprite](), (buf: ArrayBuffer[Sprite]) => buf.clear(), 1000)
  implicit val velBufPool = Pool[ArrayBuffer[Float]](() => ArrayBuffer[Float](), (buf: ArrayBuffer[Float]) => buf.clear(), 1000)
  implicit val funcTaskPool = Pool[TimedFunctionTask](100)
  implicit val interFuncTaskPool = Pool[InterpolatedFunctionTask](100)
  /** 関係なーい
  preAlloc[Sprite]()
  preAlloc[Token]()
  preAlloc[ArrayBuffer[Sprite]]()
  preAlloc[ArrayBuffer[Float]]()
  preAlloc[TimedFunctionTask]()
    * */


  val tokens = ArrayBuffer[Token[T]]()
  val skin = assets.get[Skin]("skin/default.json")
  val panelAdd = (added: Seq[Seq[ActionPuzzle[T]#AP]]) => {
    for (row <- added; p <- row) {
      val token = manual[Token[T]]
      token.init(p)
      //TODO check for alignment
      token.reactVar(p.x)(x => token.setX(calcPanelX(x)))
      token.reactVar(p.y)(y => token.setY(calcPanelY(y)))
      token.setSize(panelW, panelH)
      token.setOrigin(panelW / 2, panelH / 2)
      tokens += token
      puzzleGroup.addActor(token)
    }
  }

  val panelRemove = (removed: Seq[ActionPuzzle[T]#AP]) => {
    for (panel <- removed; token <- tokens.find(_.panel == panel)) {
      tokens -= token
      token.addAction(sequence(ExplosionFadeout(), Actions.run(new Runnable {
        def run() {
          token.free
        }
      })))
      val duration = 1f
      val buf = manual[ArrayBuffer[Sprite]]
      val velBuf = manual[ArrayBuffer[Float]]
      val ft = auto[TimedFunctionTask]
      val it = auto[InterpolatedFunctionTask]
      //make this particle specific code into trait's code
      add(ft.setFunctions(
        () => {
          //TextureUtil.split(token.sprite)(8)(8)(buf)
          val texture = assets.get[Texture]("data/particle.png")
          0 to ((score()+1)/10) foreach {
            _ => val p = manual[Sprite]
              p.setTexture(texture)
              p.setRegion(0f, 0f, 1f, 1f)
              p.setOrigin(0f, 0f)
              val s = random(3,30)
              p.setSize(s,s)
              p.setPosition(token.getX + token.getWidth / 2, token.getY + token.getHeight / 2)
              p.setColor(token.sprite.getColor)
              buf += p
          }

          Explosion.init(() => random(PI2), () => random(2000), velBuf, buf.length)
          addDrawable(buf)
        },
        Explosion.update(0, -100, 5f)(buf, velBuf),
        () => {
          removeDrawable(buf)
          buf foreach (_.free)
          buf.free
          velBuf.free
        }) in duration)
      val color = token.sprite.getColor.cpy
      val hsv = ColorUtil.ColorToHSV(color)
      hsv.v = 1f
      hsv.s = 0.7f
      color.set(hsv.toColor)
      add(it setUpdater (alpha => {
        val a = Interpolation.exp10Out.apply(0.8f, 0, alpha)
        color.a = a
        buf.foreach {
          sp => sp.setColor(color)
        }
      }) in duration * 2)

    }
  }
  this.addListener(new InputListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      val (ix, iy) = positionToIndex(x, y)
      for {ap <- puzzle.future.lift(ix).map(_.lift(iy)).flatten
           token <- tokens.find(_.panel == ap)
      } {
        log(ix, iy, ap.tx(), ap.ty(), token)
        //puzzle.removeFillUpdateTargetPosition(ap::Nil)
      }
      super.touchDown(event, x, y, pointer, button)
    }
  })
}
