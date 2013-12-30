package com.glyph.scala.game.action_puzzle

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.scala.game.action_puzzle.view.{Paneled2, Grid}
import com.glyph.scala.lib.util.reactive.{Varying, Reactor}
import com.glyph.scala.lib.libgdx.actor._
import com.badlogic.gdx.graphics.{GL10, Texture}
import com.glyph.scala.lib.util.{reactive, ColorUtil, Logging}
import com.glyph.scala.lib.util.pool.Pool
import com.badlogic.gdx.graphics.g2d.{TextureRegion, Batch, Sprite}
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.updatable.task.{InterpolatedFunctionTask, TimedFunctionTask}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.glyph.scala.lib.util.animator.Explosion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.math.{Matrix4, Interpolation, MathUtils}
import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.libgdx.actor.blend.AdditiveBlend
import aurelienribon.tweenengine.{TweenManager, Tween}
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.glyph.scala.lib.libgdx.font.FontUtil._
import scala.Some
import com.glyph.scala.lib.libgdx.WordParticle
import com.glyph.scala.lib.libgdx.gl.{UVTrail, BaseStripBatch, ShaderHandler}
import com.badlogic.gdx.Gdx
import collection.JavaConversions._
import com.badlogic.gdx.graphics.glutils.ShaderProgram

class MyTrail() extends UVTrail(5)

/**
 * @author glyph
 */
class APView[T](score: Varying[Int], puzzle: ActionPuzzle[T], assets: AssetManager)
  extends Group
  with Reactor
  with Logging
  with Tasking
  with SpriteBatchRenderer
  with AdditiveBlend
  with Scissor
  with Paneled2 {

  //TODO i want to remove all those drawing specific codes such as sprite,token,anything..
  //TODO there is 1mb of allocation when the panel is removed,added
  //the tuple2 shows the largest memory usage.
  implicit val _1 = classOf[MyTrail]
  implicit val _2 = classOf[Sprite]
  implicit val _3 = classOf[Token[T]]
  implicit val poolingTrail = genPooling[MyTrail]
  implicit val trailPool = Pool[MyTrail](10000)
  implicit val spritePool = Pool[Sprite](10000)
  implicit val spriteActorPool = Pool[SpriteActor](100)
  implicit val tokenPool = Pool[Token[T]](() => new Token[T](null,null), (tgt: Token[T]) => tgt.resetForPool(), row * column * 2)
  implicit val bufPool = Pool[ArrayBuffer[Sprite]](() => ArrayBuffer[Sprite](), (buf: ArrayBuffer[Sprite]) => buf.clear(), 1000)
  implicit val velBufPool = Pool[ArrayBuffer[Float]](() => ArrayBuffer[Float](), (buf: ArrayBuffer[Float]) => buf.clear(), 1000)
  implicit val tftPool = Pool[TimedFunctionTask](1000)
  implicit val iftPool = Pool[InterpolatedFunctionTask](1000)

  log("new APView")
  ShaderProgram.pedantic = false
  preAlloc[MyTrail](1000)
  preAlloc[Sprite](1000)
  preAlloc[Token[T]](100)

  val spriteTrailArray = new com.badlogic.gdx.utils.Array[Seq[(Sprite, MyTrail)]](1000)
  val shader = ShaderHandler("shader/rotate2.vert", "shader/default.frag")
  val batch = new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES)
  val dummyTex = assets.get[Texture]("data/particle.png")
  val combined = new Matrix4
  val tupleRenderer = (s: ShaderProgram)=>(tuple: (Sprite, MyTrail)) => {
    val sp = tuple._1
    val trail = tuple._2
    trail.add(sp.getX + sp.getWidth / 2, sp.getY + sp.getHeight / 2)
    batch.draw(s, trail.meshVertices, trail.count)
  }
  val trailRenderer = shader.applier2 {
    s => {
       val seqRenderer = tupleRenderer(s)
      () => {
        Gdx.gl.glEnable(GL10.GL_TEXTURE_2D)
        Gdx.gl.glEnable(GL10.GL_BLEND)
        Gdx.gl.glBlendFunc(SRC_FUNC, DST_FUNC)
        s.begin()
        s.setUniformMatrix("u_projTrans", combined.set(getStage.getSpriteBatch.getProjectionMatrix).mul(computeTransform()))
        s.setUniformi("u_texture", 0)
        dummyTex.bind()
        batch.begin()
        val itr = spriteTrailArray.iterator()
        while(itr.hasNext){itr.next() foreach seqRenderer}
        batch.end(s)
        s.end()
      }
    }
  }
  val gridFunctions = RVJSON(GdxFile("json/grid.json")).map(_.flatMap(Grid(_)))
  val alphaToIndex = gridFunctions map (_.map {
    case (fx, fy) => Grid.alphaToIndex(fx, fy)
  })
  val swipeChecker = gridFunctions map (_.map {
    case (fx, fy) => genSwipeChecker(fx, fy)
  })
  val swipeStopper = genSwipeStopper

  import Pool._
  import Actions._
  import MathUtils._

  def row: Int = puzzle.ROW

  def column: Int = puzzle.COLUMN


  val font = internalFont("font/corbert.ttf", 50)
  implicit val renderer = this
  implicit val manager = new TweenManager
  Tween.registerAccessor(classOf[Sprite], SpriteAccessor)
  Tween.setCombinedAttributesLimit(4)
  val tokens = new com.badlogic.gdx.utils.Array[Token[T]]()
  val skin = assets.get[Skin]("skin/holo/Holo-dark-xhdpi.json")

  private val tokenInitializer = (p:ActionPuzzle[T]#AP)=>{
    val token = manual[Token[T]]
    val spriteActor = manual[SpriteActor]
    val texture = assets.get[Texture]("data/round_rect.png")
    spriteActor.sprite.setTexture(texture)
    spriteActor.sprite.asInstanceOf[TextureRegion].setRegion(0,0,texture.getWidth,texture.getHeight)
    token.init(p,spriteActor)
    tokens add token
    addActor(token)
  }
  def updateTokenPosition(delta:Float){
    if(gridFunctions().isDefined){
      val(fx,fy) = gridFunctions().get
      val itr = tokens.iterator()
      while(itr.hasNext){
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

  private val seqTokenInitializer = (seq:Seq[ActionPuzzle[T]#AP])=> seq foreach tokenInitializer
  val panelAdd = (added: Seq[Seq[ActionPuzzle[T]#AP]]) => added foreach seqTokenInitializer
  def findTokenByPanel(p:ActionPuzzle[T]#AP):Token[T] = {
    var result:Token[T] = null
    val itr = tokens.iterator()
    while(itr.hasNext && result == null){
      val token = itr.next()
      if(p == token.panel) result = token
    }
    result
  }

  val panelRemove = (removed: IndexedSeq[ActionPuzzle[T]#AP]) => {
    var i = 0
    val size = removed.size
    while(i < size){
      val token = findTokenByPanel(removed(i))
      if(token != null){
        tokens.removeValue(token,true)
        token.clearReaction() //this must be done since the panel is immediately removed
        token.addAction(sequence(ExplosionFadeout(), Actions.run(new Runnable {
          def run() { //I need to take care of this allocation eventually
            token.tgtActor.asInstanceOf[SpriteActor].free
            token.free
          }
        })))
      }
      i += 1
    }
  }

  private val widthFolder = (f: Float, s: Sprite) => f + s.getWidth
  private val heightFolder = (f: Float, s: Sprite) => f + s.getHeight

  def halfW(seq: Seq[Sprite]) = (0f /: seq)(widthFolder) / 2f

  def meanH(seq: Seq[Sprite]) = (0f /: seq)(heightFolder) / seq.size

  def showScoreParticle(token: Token[T], score: Int) {
    val sprites = WordParticle.StringToSprites(font)(score + "")(0.7f)
    WordParticle.start(sprites, WordParticle.popSprites(sprites)(token.getX + token.getWidth / 2f - halfW(sprites), token.getY, () => token.getHeight / 2 - meanH(sprites), 0.7f))
  }

  def addParticles(token: Token[T]) {
    implicit val bufCls = classOf[ArrayBuffer[Sprite]]
    val duration = 1f
    val buf = manual[ArrayBuffer[Sprite]]
    val velBuf = manual[ArrayBuffer[Float]]
    val ft = auto[TimedFunctionTask]
    val it = auto[InterpolatedFunctionTask]
    //make this particle specific code into trait's code
    var setBuf: Seq[(Sprite, MyTrail)] = null
    add(ft.setFunctions(
      () => {
        //TextureUtil.split(token.sprite)(8)(8)(buf)
        val texture = assets.get[Texture]("data/particle.png")
        0 to (score() / 100) foreach {
          _ => val p = manual[Sprite]
            p.setTexture(texture)
            p.setRegion(0f, 0f, 1f, 1f)
            p.setOrigin(0f, 0f)
            val s = random(3, 30)
            p.setSize(s, s)
            p.setPosition(token.getX + token.getWidth / 2, token.getY + token.getHeight / 2)
            p.setColor(token.tgtActor.getColor)
            buf += p
        }
        Explosion.init(() => random(PI2), () => random(2000), velBuf, buf.length)
        addDrawable(buf)
        setBuf = buf map (sp => sp -> manual[MyTrail])
        spriteTrailArray.add(setBuf)
      },
      Explosion.update(0, -100, 5f)(buf, velBuf),
      () => {
        removeDrawable(buf)
        buf foreach (_.free)
        buf.free
        setBuf foreach {
          case (sp, trail) => trail.free
        }
        spriteTrailArray.removeValue(setBuf, true)
        velBuf.free

      }) in duration)
    val color = token.tgtActor.getColor.cpy
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

  /*
  this.addListener(new InputListener() {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      for (ai <- alphaToIndex()) {
        val (ix, iy) = ai(x / getWidth, y / getHeight) //positionToIndex(x, y)
        for {ap <- puzzle.future.lift(ix).map(_.lift(iy)).flatten
             token <- tokens.find(_.panel == ap)
        } {
          log(ix, iy, ap.tx(), ap.ty(), token)
          //puzzle.removeFillUpdateTargetPosition(ap::Nil)
        }
      }
      super.touchDown(event, x, y, pointer, button)
    }
  })
*/
  override def act(delta: Float) {
    super.act(delta)//this call causes Some allocation!
    updateTokenPosition(delta)
    manager.update(delta)
  }

  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    super.draw(batch, parentAlpha)
    batch.end()
    trailRenderer()
    batch.begin()
  }
}
