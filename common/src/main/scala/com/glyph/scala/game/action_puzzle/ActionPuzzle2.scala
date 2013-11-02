package com.glyph.scala.game.action_puzzle

import scalaz._
import Scalaz._
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.game.action_puzzle.GMatch3.Panel
import com.glyph.scala.lib.util.reactive.Var
import com.badlogic.gdx.math.{Interpolation, Vector2, MathUtils}
import com.glyph.scala.lib.util.updatable.task._
import scala.util.Try
import scala.Some
import com.glyph.scala.lib.util.updatable.reactive.Animator

/**
 * @author glyph
 */
class ActionPuzzle2 extends Logging {

  import GMatch3._

  //inner values
  val gravity = new Vector2(0, -10f)
  val processor = new ParallelProcessor {}
  val SIZE = 6
  val puzzle: Var[GMatch3.Puzzle[P]] = Var(Vector(0 until SIZE map (_ => Vector()): _*))
  val seed = () => MathUtils.random(0, 5) |> (new P(_))

  //callbacks
  var addPanel = (panel: P, x: Int, y: Int) => {}
  var removePanel = (panel: P) => {}
  var startSwipeCheck = (callback: (Int, Int, Int, Int) => Unit) => {}
  var stopSwipeCheck = () => {}

  //methods
  def update(delta: Float) {
    processor.update(delta)
    for (row <- puzzle(); p <- row) {
      p.update(delta)
    }
  }

  def initialize() {
    fill()
    startSwipeCheck {
      (ax, ay, bx, by) => {
        log("swiped", ax, ay, bx, by)
        val attempt = Try {
          import Animator._
          //TODO ease panels into swapped place
          val pa = puzzle()(ax)(ay)
          val pb = puzzle()(bx)(by)
          if (pa.stopped() && pb.stopped()) {
            pa.stopped() = false
            pb.stopped() = false
            pa.modifying() = true
            pb.modifying() = true
            val moves = (pa.x, bx) ::(pa.y, by) ::(pb.x, ax) ::(pb.y, ay) :: Nil map {
              case (v, p) => interpolate(v) to p in 0.3f using Interpolation.exp10Out
            }
            processor.add(Sequence(WaitAll(moves: _*), Do {
              puzzle() = puzzle().updated(ax, puzzle()(ax).updated(ay, pb))
              puzzle() = puzzle().updated(bx, puzzle()(bx).updated(by, pa))
              pa.stopped() = true
              pb.stopped() = true
              pa.modifying() = false
              pb.modifying() = false
              scanRemoveFill()
            }))
          }
        }
        for (e <- attempt.failed) {
          err(e)
        }
      }
    }
  }

  def scanRemoveFill() {
    val scanned = puzzle().scanAll
    remove(scanned.flatten.map {
      case (p, x, y) => p
    }.distinct)
    fill()
  }

  def fill() {
    val filling = puzzle().createFillingPuzzle(seed, SIZE)
    puzzle() = puzzle() append filling
    for (row <- filling; p <- row) {
      val (x, y) = puzzle().indexOfPanelUnhandled(p)
      addPanel(p, x, y)
      p.x() = x
      p.y() = SIZE
      p.stopped() = false
    }
    resetAllNext()
  }

  def remove(panels: Seq[P]) {
    puzzle() = puzzle() map (_.filterNot(panels.contains))
    panels foreach removePanel
    resetAllNext()
  }

  def resetAllNext() {
    for (row <- puzzle()) {
      for ((p, i) <- row.zipWithIndex) {
        p.nextOpt = if (i > 0) Some(row(i - 1)) else None
      }
    }
  }

  class P(n: Int) extends IntPanel(n) {
    val x = Var(0f)
    val y = Var(0f)
    val vx = Var(0f)
    val vy = Var(0f)
    val stopped = Var(false)
    val modifying = Var(false)
    var nextOpt: Option[P] = None
    var onStop = () => {
      scanRemoveFill()
    }

    def update(delta: Float) {
      if (!modifying()) {
        if (!stopped()) {
          vx() += gravity.x * delta
          vy() += gravity.y * delta
          val nx = x() + vx() * delta
          var ny = y() + vy() * delta
          nextOpt match {
            case Some(next) => if (ny - next.y() < 1) {
              ny = next.y() + 1 //常に上にいると仮定
              if (next.stopped()) {
                //TODO stop and do scan!
                stopped() = true
                onStop()
              }
            }
            case None => if (ny < 0) {
              ny = 0
              stopped() = true
              onStop()
            }
          }
          x() = nx
          y() = ny
        } else {
          nextOpt match {
            case Some(next) => if (y() - next.y() > 1) {
              stopped() = false
            }
            case None => if (y() > 0) {
              stopped() = false
            }
          }
        }
      }
    }

    override def matchTo(panel: Panel): Boolean = super.matchTo(panel) && stopped()
  }

  object P {
  }

}

class IntPanel(val n: Int) extends GMatch3.Panel {
  def matchTo(panel: Panel): Boolean = panel match {
    case p: IntPanel => p.n == n
    case _ => false
  }
}

/*
import java.awt.event.{KeyEvent, MouseEvent}
import java.util.Random
import org.jbox2d.callbacks.ContactFilter
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics._
import processing.core.PApplet
import twitter4j.User

/**
 * @author glyph
 */
class Visualizer extends PApplet {
  val delta = 0.016f
  val world = new World(new Vec2(0, 0f))
  var offset = new Vec2
  world.setContactFilter(new ContactFilter {
    override def shouldCollide(fixtureA: Fixture, fixtureB: Fixture) = false
  })

  import Clustering.{CLeaf, CTree, CNode}

  val root: Tree = Clustering.clusteringTree(400) match {
    case CNode(l, r) => sepNode(l, r)
    case leaf: CLeaf => toLeaf(leaf)
  }

  implicit def sepNode(l: CTree, r: CTree): Node = (l, r) match {
    case (cl: CNode, cr: CNode) => Node(sepNode(cl.left, cl.right), sepNode(cr.left, cr.right))
    case (cl: CNode, cr: CLeaf) => Node(sepNode(cl.left, cl.right), cr)
    case (cl: CLeaf, cr: CNode) => Node(cl, sepNode(cr.left, cr.right))
    case (cl: CLeaf, cr: CLeaf) => Node(cl, cr)
  }

  implicit def toLeaf(l: CLeaf): Leaf = {
    new UserToken(l.user.user)
  }

  val objects = root.flatten.toArray[Tree]
  val rand = new Random()

  def rRand(b: Float, e: Float) = b + rand.nextFloat() * (e - b)

  objects.foreach {
    _.body.setTransform(new Vec2(rRand(0, 200), rRand(0, 200)), 0)
  }

  val springs = objects.collect {
    case n@Node(l, r) => n.rSpring :: n.lSpring :: Nil
  }.flatten

  override def mouseClicked(e: MouseEvent) {
    super.mouseClicked(e)
    println("clicked")
    root.body.setTransform(new Vec2(e.getX - width / 2, e.getY - height / 2), 0)
  }


  def keyCheck() {
    import KeyEvent._
    val speed = 10
    if (keyPressed) {
      keyCode match {
        case VK_RIGHT => offset.x += speed
        case VK_LEFT => offset.x -= speed
        case VK_DOWN => offset.y += speed
        case VK_UP => offset.y -= speed
        case _ =>
      }
    }
  }

  override def sketchFullScreen(): Boolean = true

  override def setup() {
    super.setup()
    println("setup")
    frameRate(60)
    size(displayWidth, displayHeight)
    smooth()
  }

  override def draw() {
    background(150)
    val mean = objects.map(_.body.getPosition).reduce((a, b) => a.add(b)).mul(1f / objects.length.toFloat)
    keyCheck()
    translate(-mean.x - offset.x, -mean.y - offset.y)
    translate(width / 2, height / 2)
    Coulomb.step(objects)
    Spring.step(springs)
    world.step(delta, 10, 10)
    springs.foreach {
      _.draw()
    }
    objects.foreach {
      _.draw()
    }
  }

  object Coulomb {
    val distInv = new Vec2

    def step(objects: Array[Tree]) {
      var a, b: Body = null
      var apos, bpos: Vec2 = null
      var dist: Vec2 = null
      val COULOMB = 10000f
      val length = objects.length
      var distSqLen: Float = 0
      var coulomb = 0f
      var i, j = 0
      while (i < length) {
        j = i + 1
        a = objects(i).body
        apos = a.getPosition
        while (j < length) {
          b = objects(j).body
          bpos = b.getPosition
          dist = apos.sub(bpos)
          distSqLen = dist.lengthSquared()
          dist.normalize()
          if (distSqLen != 0) {
            coulomb = COULOMB / distSqLen
            dist.mulLocal(-coulomb)
            distInv.set(-dist.x, -dist.y)
            a.applyForce(distInv, apos)
            b.applyForce(dist, bpos)
          }
          j += 1
        }
        i += 1
      }
    }
  }

  object Tree {
    val bodyDef = new BodyDef
    bodyDef.`type` = BodyType.DYNAMIC
    val shape = new CircleShape
    shape.setRadius(5)
    val fixDef = new FixtureDef
    fixDef.shape = shape
    fixDef.density = 0.001f
    fixDef.friction = 0.3f
    fixDef.restitution = 0.5f
  }

  trait Tree {
    val body: Body
    val radius: Float

    def draw() {
      pushMatrix()
      val pos = body.getPosition
      translate(pos.x, pos.y)
      color(0)
      noFill()
      ellipse(0, 0, radius * 2, radius * 2)
      popMatrix()
    }

    def flatten: List[Tree]
  }

  object Spring {
    val dist = new Vec2
    val distInv = new Vec2

    def step(springs: Array[Spring]) {
      var i = 0
      val l = springs.length
      while (i < l) {
        springs(i)()
        i += 1
      }
    }
  }

  case class Spring(a: Body, b: Body) {

    import Spring._

    def apply() {
      val ap = a.getPosition
      val bp = b.getPosition
      dist.set(ap.sub(bp))
      dist.mulLocal(5f) //spring
      distInv.set(dist).mulLocal(-1)
      a.applyForce(distInv, ap)
      b.applyForce(dist, bp)
    }

    def draw() {
      val ap = a.getPosition
      val bp = b.getPosition
      line(ap.x, ap.y, bp.x, bp.y)
    }
  }

  case class Node(right: Tree, left: Tree) extends Tree {
    val body: Body = world.createBody(Tree.bodyDef)
    body.createFixture(Tree.fixDef)
    val radius: Float = Tree.shape.getRadius
    val rSpring = Spring(body, right.body)
    val lSpring = Spring(body, left.body)

    def flatten: List[Tree] = Node.this :: Nil ::: right.flatten ::: left.flatten
  }

  object Leaf {
    val shape = new CircleShape
    shape.setRadius(15)
    val fixDef = new FixtureDef
    fixDef.shape = shape
    fixDef.density = 0.0001f
    fixDef.friction = 0.1f
    fixDef.restitution = 0.5f
  }

  case class Leaf() extends Tree {
    val body: Body = world.createBody(Tree.bodyDef)
    body.createFixture(Leaf.fixDef)
    val radius: Float = Leaf.shape.getRadius

    def flatten: List[Tree] = Leaf.this :: Nil
  }

  class UserToken(user: User) extends Leaf() {
    println("image:" + user.getMiniProfileImageURL)
    val img = loadImage(user.getMiniProfileImageURL)
    val w = Leaf.shape.getRadius * 2 * Math.sqrt(2).toFloat / 2f - 1

    override def draw() {
      super.draw()
      val pos = body.getPosition
      fill(0)
      if (img != null) image(img, pos.x - w / 2, pos.y - w / 2, w, w)
      text(user.getName, pos.x, pos.y)
    }
  }

}

object Visualizer {
  def main(args: Array[String]) {
    //PApplet.main("Visualizer",args)
    PApplet.main("Visualizer")
  }
}
 */
