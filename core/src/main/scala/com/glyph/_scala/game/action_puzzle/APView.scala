package com.glyph._scala.game.action_puzzle

import com.glyph._scala.game.action_puzzle.view.{Paneled2, Grid}
import com.glyph._scala.lib.util.reactive.Reactor
import com.glyph._scala.lib.libgdx.actor._
import com.glyph._scala.lib.util.Logging
import com.glyph._scala.lib.util.pool.{Pooling, Pool}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.{Actor, Group}
import com.glyph._scala.lib.util.json.RVJSON
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import com.glyph._scala.game.Glyphs
import Glyphs._
import com.glyph._scala.lib.libgdx.gl._
import scala.reflect.ClassTag
import com.glyph._scala.lib.libgdx.actor.table.Layers
import com.glyph._scala.lib.libgdx.actor.widgets.Layered

class MyTrail() extends UVTrail(5)
/**
 * @author glyph
 */
class APView[T, A <: Actor : Pooling:Class](puzzle: ActionPuzzle[T])
  extends Group
  with Reactor
  with Logging
  with Tasking
  with Layered
  with Scissor
  with Paneled2 {

  //TODO i want to remove all those drawing specific codes such as sprite,token,anything..
  //TODO this class must be a layouting class!
  //TODO there is 1mb of allocation when the panel is removed,added
  implicit val spriteActorPool = Pool[A](100)
  //this pool can never be made global since the target uses an inner class
  implicit val tokenPool = Pool[Token[T,A]](
    () => new Token[T,A](null, null.asInstanceOf[A]))(
      (tgt: Token[T,A]) => tgt.resetForPool())(row * column * 2)
  spriteActorPool.preAlloc(6*6)
  tokenPool.preAlloc(6*6)

  var tokenRemove = (token: Token[T,A]) => {}
  val gridFunctions = RVJSON(GdxFile("json/grid.json")).map(_.flatMap(Grid(_)))
  val panelLayer = new Group
  addActor(panelLayer)
  val alphaToIndex = gridFunctions map (_.map {
    case (fx, fy) => Grid.alphaToIndex(fx, fy)
  })
  val swipeChecker = gridFunctions map (_.map {
    case (fx, fy) => genSwipeChecker(fx, fy)
  })
  val swipeStopper = genSwipeStopper

  import Actions._

  def row: Int = puzzle.ROW

  def column: Int = puzzle.COLUMN


  val tokens = new com.badlogic.gdx.utils.DelayedRemovalArray[Token[T,A]]()

  private val tokenInitializer = (p: ActionPuzzle[T]#AP) => {
    val token = manual[Token[T,A]]
    token.init(p, manual[A])
    tokens add token
    panelLayer.addActor(token)
  }

  def updateTokenPosition(delta: Float) {
    if (gridFunctions().isDefined) {
      val fs = gridFunctions().get
      val fx = fs._1
      val fy = fs._2
      val itr = tokens.iterator()
      while (itr.hasNext) {
        val t = itr.next
        val p = t.panel
        t.setX(fx.indexToAlpha(p.x()) * getWidth)
        t.setY(fy.indexToAlpha(p.y()) * getHeight)
        val w = fx.tokenSize * getWidth
        val h = fy.tokenSize * getHeight
        t.setSize(w, h)
        t.setOrigin(w / 2, h / 2)
      }
    }
  }

  private val seqTokenInitializer = (seq: Seq[ActionPuzzle[T]#AP]) => seq foreach tokenInitializer
  val panelAdd = (added: Seq[Seq[ActionPuzzle[T]#AP]]) => added foreach seqTokenInitializer

  def findTokenByPanel(p: ActionPuzzle[T]#AP): Token[T,A] = {
    var result: Token[T,A] = null
    val itr = tokens.iterator()
    while (itr.hasNext && result == null) {
      val token = itr.next()
      if (p == token.panel) result = token
    }
    result
  }
  def removeToken(token:Token[T,A]){
    tokens.removeValue(token, true)
    token.clearReaction() //this must be done since the panel is immediately removed
    token.addAction(sequence(ExplosionFadeout(), Actions.run(new Runnable {
      def run() {
        //I need to take care of this allocation eventually
        token.tgtActor.free
        token.free
      }
    })))
    onTokenRemove(token)
    tokenRemove(token)
  }
  def removeAllToken(){
    tokens.begin()
    val it = tokens.iterator()
    while(it.hasNext){
      removeToken(it.next)
    }
    tokens.end()
  }

  protected def onTokenRemove(token:Token[T,A]){}
  val panelRemove = (removed: IndexedSeq[ActionPuzzle[T]#AP]) => {
    var i = 0
    val size = removed.size
    while (i < size) {
      val token = findTokenByPanel(removed(i))
      if (token != null) {
        removeToken(token)
      }
      i += 1
    }
  }

  override def act(delta: Float) {
    super.act(delta) //this call causes Some allocation!
    updateTokenPosition(delta)
  }
}
