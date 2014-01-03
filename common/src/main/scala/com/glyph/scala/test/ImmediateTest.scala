package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.util.screen.{GlyphScreen => GScreen}
import com.badlogic.gdx.graphics.glutils.{ShapeRenderer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics.{GL10, Texture, Color, GL20}
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}
import scala.collection.mutable.ArrayBuffer
import com.glyph.scala.lib.util.{Threading, Logging}
import com.badlogic.gdx.math._
import com.badlogic.gdx.{Input, Gdx}
import com.glyph.scala.lib.libgdx.actor.SpriteActor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * @author glyph
 */
class ImmediateTest extends ScreenBuilder {
  def requirements: Set[(Class[_], Seq[String])] = Set(classOf[Texture] -> Seq("data/sword.png", "data/dummy.png", "data/particle.png"))

  def create(implicit assetManager: AssetManager): GScreen = new ConfiguredScreen with Logging with Threading {
    val renderer = new ImmediateModeRenderer20(false, true, 1)
    val pRenderer = new ShapeRenderer(30000)
    val texture = assetManager.get[Texture]("data/particle.png")
    backgroundColor = Color.BLACK
    val MAX_RECORD = 10000
    val records = new ArrayBuffer[Vector2]
    val lines = new ArrayBuffer[Float]()
    val vertices = new Array[Float](MAX_RECORD * 4)
    val texCoordsV = new Array[Float](MAX_RECORD * 2)
    val tmp = new Vector2
    val m = new Matrix3()
    var drawWire = false
    //TODO try bezier to draw smooth spline!
    val bezier = new com.badlogic.gdx.math.Bezier[Vector2]()

    def bezierToLine(src: Bezier[Vector2])(dst: ArrayBuffer[Float]) {
      dst.clear()
      val N = 1000
      var i = 0
      val tmp = new Vector2()
      while (i < N) {
        src.valueAt(tmp, i / N.toFloat)
        dst += tmp.x
        dst += tmp.y
        i += 1
      }
    }
    def testCross(ax: Float, ay: Float, bx: Float, by: Float) = ax * by - ay * bx > 0
    def testSharp(ax: Float, ay: Float, bx: Float, by: Float) = ax * bx + ay * by > 0


    def setupVertices(src: ArrayBuffer[Float])(dst: Array[Float], tmpVec: Vector2) {
      //TODO check intersection
      var i = 0
      val l = src.length / 2
      val W = 50f
      var flip = false
      while (i < l) {
        if (i > 0) {
          val vi = i * 2
          val di = i * 4
          val px = lines(vi - 2)
          val py = lines(vi - 1)
          val x = lines(vi)
          val y = lines(vi + 1)
          import MathUtils._
          import Math.min
          val deg = atan2(x - px, y - py) * radDeg
          val width = W

          {
            tmpVec.set(-width, 0)
            tmpVec.rotate(-deg)
            if (vi == 2) {
              tmpVec.add(px, py)
              dst(0) = tmpVec.x
              dst(1) = tmpVec.y
              tmpVec.sub(px, py)
            }
            tmpVec.add(x, y)
            dst(if (flip) di + 2 else di) = tmpVec.x
            dst(if (flip) di + 3 else di + 1) = tmpVec.y
          }

          {
            tmpVec.set(width, 0)
            tmpVec.rotate(-deg)
            if (vi == 2) {
              tmpVec.add(px, py)
              dst(2) = tmpVec.x
              dst(3) = tmpVec.y
              tmpVec.sub(px, py)
            }
            tmpVec.add(x, y)
            dst(if (flip) di else di + 2) = tmpVec.x
            dst(if (flip) di + 1 else di + 3) = tmpVec.y
          }
          //check intersection and correct them


          //check intersectionここがまちがっている
          /*
          {
            val px1 = dst(di - 4)
            val py1 = dst(di - 3)
            val px2 = dst(di - 2)
            val py2 = dst(di - 1)
            val x1 = dst(di)
            val y1 = dst(di + 1)
            val x2 = dst(di + 2)
            val y2 = dst(di + 3)
            import com.badlogic.gdx.math.Intersector._
            if (intersectLines(px1, py1, px2, py2, x1, y1, x2, y2, tmpVec)) {
              if (testCross(px1 - px2, py1 - py2, x1 - x2, y1 - y2)) {
                dst(di) = px1
                dst(di + 1) = py1
              } else {
                dst(di + 2) = px2
                dst(di + 3) = py2
              }
            }
          }
          */


          //TODO you need to switch left/right when the degree is between 90 and 270
          if (i > 1) {
            val ppx = lines(vi - 4)
            val ppy = lines(vi - 3)
            if (!testSharp(x - px, y - py, px - ppx, py - ppy)) {
              val px1 = dst(di - 4)
              val py1 = dst(di - 3)
              val px2 = dst(di - 2)
              val py2 = dst(di - 3)
              val tlx = px2 - px1 //top line x
              val tly = py2 - py1 //top line y
              val x1 = dst(di)
              val y1 = dst(di + 1)
              val x2 = dst(di + 2)
              val y2 = dst(di + 3)
              if (flip == testCross(tlx, tly, x1 - px1, y1 - py1) && flip == testCross(tlx, tly, x2 - px1, y2 - py1)) {
                //if both verts are right bf topline...
                log("flip!")
                var tmp = dst(di)
                dst(di) = dst(di + 2)
                dst(di + 2) = tmp
                tmp = dst(di + 1)
                dst(di + 1) = dst(di + 3)
                dst(di + 3) = dst(di + 1)
                flip = !flip
              }
            }
          }
        }
        i += 1
      }
    }

    /**
     *
     * @param vertices array of(x1,y1,x2,y2....)
     * @param dst array of (v1,v2,v1,v2...)
     * @param tmpVec
     * @return
     */
    def setUpUVs(vertices: Array[Float], numVertices: Int)(dst: Array[Float])(tmpVec: Vector2) {
      var l1 = 0f
      var l2 = 0f

      {
        //get the total length
        var i = 0
        val l = numVertices
        while (i < l) {
          val vi = i * 4
          val x1 = vertices(vi)
          val y1 = vertices(vi + 1)
          val x2 = vertices(vi + 2)
          val y2 = vertices(vi + 3)
          if (i >= 4) {
            val px1 = vertices(vi - 4)
            val py1 = vertices(vi - 3)
            val px2 = vertices(vi - 2)
            val py2 = vertices(vi - 1)
            l1 += tmpVec.set(x1 - px1, y1 - py1).len()
            l2 += tmpVec.set(x2 - px2, y2 - py2).len()
          }
          i += 1
        }
      }

      {
        //now calculate the v of uvs
        var i = 0
        val l = numVertices
        var pos1 = 0f
        var pos2 = 0f
        while (i < l) {
          val vi = i * 4
          val ti = i * 2
          val x1 = vertices(vi)
          val y1 = vertices(vi + 1)
          val x2 = vertices(vi + 2)
          val y2 = vertices(vi + 3)
          if (i >= 4) {
            val px1 = vertices(vi - 4)
            val py1 = vertices(vi - 3)
            val px2 = vertices(vi - 2)
            val py2 = vertices(vi - 1)
            pos1 += tmpVec.set(x1 - px1, y1 - py1).len()
            pos2 += tmpVec.set(x2 - px2, y2 - py2).len()
            dst(ti) = pos1 / l1
            dst(ti + 1) = pos2 / l2
          } else {
            dst(0) = 0
            dst(1) = 0
          }
          i += 1
        }
      }
    }

    val follower = new SpriteActor {
      var prevX = 0f
      var prevY = 0f

      override def act(delta: Float): Unit = {
        super.act(delta)
        if (getX != prevX || getY != prevY) {
          // records += getX
          // records += getY
          prevX = getX
          prevY = getY
        }
      }
    }
    follower.sprite.setTexture(texture)
    follower.sprite.asInstanceOf[TextureRegion].setRegion(0, 0, texture.getWidth, texture.getHeight)
    follower.setSize(100, 100)
    //follower.setOrigin(-50,50)
    follower.setColor(Color.GREEN)
    root.addActor(follower)
    stage.addListener(new InputListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
        follower.clearActions()
        import Actions._
        import Interpolation._
        import MathUtils._
        follower.addAction(moveTo(x, y, 1f, exp10Out))
        log(atan2(x, y) * radDeg)
        log(testCross(0,100,x-STAGE_WIDTH/2,y-STAGE_HEIGHT/2))
        true
      }

      override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
        follower.clearActions()
        import Actions._
        import Interpolation._
        follower.addAction(moveTo(x, y, 1f, exp10Out))

        /*
        records += new Vector2(x, y)
        if (records.length > 2) {
          bezier.set(records.toArray, 0, records.length)
          bezierToLine(bezier)(lines)
        }*/
        lines += x
        lines += y
        /*
        if (lines.length < MAX_RECORD - 2) {
          lines += x
          lines += y
        }
        */
        super.touchDragged(event, x, y, pointer)
      }

      override def keyDown(event: InputEvent, keycode: Int): Boolean = keycode match {
        case Input.Keys.R => lines :: records :: Nil foreach (_.clear()); true
        case Input.Keys.W => drawWire = !drawWire; true
        case _ => false
      }
    })
    /*
    val SRC_FUNC: Int = GL10.GL_SRC_ALPHA

    val DST_FUNC: Int = GL10.GL_ONE
*/
    val SRC_FUNC: Int = GL10.GL_SRC_ALPHA

    val DST_FUNC: Int = GL10.GL_ONE_MINUS_SRC_ALPHA
    var pos = 0f
    var theta = 0f
    val rad = 300f

    override def render(delta: Float) {
      super.render(delta)
      /*
      pos += 100/1f*delta
      import MathUtils._
      theta += MathUtils.PI2/1 * delta
      records += sin(theta)*rad+pos
      records += cos(theta)*rad + STAGE_HEIGHT/2
*/

      Gdx.gl.glEnable(GL10.GL_TEXTURE_2D)
      texture.bind()
      // texture2.bind()
      Gdx.gl.glEnable(GL10.GL_BLEND)
      Gdx.gl.glBlendFunc(SRC_FUNC, DST_FUNC)


      drawStripe2()
      if (drawWire) drawStripeVertices()
      /*
      val width = 300
      renderer.begin(camera.combined, GL10.GL_TRIANGLES);
      renderer.texCoord(0, 0);
      renderer.color(1, 0, 0, 1);
      renderer.vertex(-0.5f*width, -0.5f*width, 0);
      renderer.texCoord(1, 0);
      renderer.color(0, 1, 0, 1);
      renderer.vertex(0.5f*width, -0.5f*width, 0);
      renderer.texCoord(0.5f, 1);
      renderer.color(0, 0, 1, 1);
      renderer.vertex(0f*width, 0.5f*width, 0);
      renderer.end();
      */
    }

    def drawStripe2() {
     log("start")
      val color = Color.RED
      val r = renderer
      setupVertices(lines)(vertices, tmp)
      setUpUVs(vertices, lines.length / 2)(texCoordsV)(tmp)
      r.begin(stage.getCamera.combined, GL20.GL_TRIANGLE_STRIP)

      val l = lines.length / 2
      var i = 0
      while (i < l) {
        val vi = i * 4
        val ti = i * 2
        val x1 = vertices(vi)
        val y1 = vertices(vi + 1)
        val x2 = vertices(vi + 2)
        val y2 = vertices(vi + 3)
        val v1 = texCoordsV(ti)
        val v2 = texCoordsV(ti + 1)
        r.color(color.r, color.g, color.b, color.a)
        r.texCoord(0, v1)
        r.vertex(x1, y1, 0)
        r.color(color.r, color.g, color.b, color.a)
        r.texCoord(1, v2)
        r.vertex(x2, y2, 0)
        i += 1
      }
      r.end()
     log("end")
    }

    def drawStripeVertices() {
      val p = pRenderer
      val color = Color.GREEN
      setupVertices(lines)(vertices, tmp)
      setUpUVs(vertices, lines.length / 2)(texCoordsV)(tmp)
      p.setColor(color)
      p.setProjectionMatrix(stage.getCamera.combined)
      p.begin(ShapeRenderer.ShapeType.Line)
      if (lines.length > 2) {
        p.polyline(vertices, 0, lines.length * 2)
      }
      p.end()
    }

    def drawRect() {
      val camera = stage.getCamera
      val r = renderer
      r.begin(camera.combined, GL20.GL_TRIANGLE_STRIP)
      val color = Color.WHITE
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(0, 0)
      r.vertex(0, 0, 0)

      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(1, 0)
      r.vertex(100, 0, 0)

      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(0, 1)
      r.vertex(0, 100, 0)

      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(1, 1)
      r.vertex(100, 100, 0)

      //drawStripe()
      r.end()
    }

    def drawStripe() {
      val r = renderer
      var i = 0
      val l = lines.length
      val color = Color.RED
      val width = 100
      val camera = stage.getCamera
      r.begin(camera.combined, GL20.GL_TRIANGLE_STRIP)
      val totalLength = {
        var result = 0f
        var i = 0
        val l = lines.length
        while (i < l) {
          if (i > 2) {
            result += tmp.set(lines(i - 2) - lines(i), lines(i - 1) - lines(i + 1)).len()
          }
          i += 2
        }
        result
      }

      var distance = 0f
      while (i < l) {
        val px = lines(if (i > 2) i - 2 else i)
        val py = lines(if (i > 2) i - 1 else i + 1)
        val x = lines(i)
        val y = lines(i + 1)
        distance += tmp.set(px - x, py - y).len()
        val deg = MathUtils.atan2(x - px, y - py) * MathUtils.radDeg
        tmp.set(-width, 0)
        tmp.rotate(-deg)
        tmp.add(x, y)
        val x1 = tmp.x
        val y1 = tmp.y
        tmp.set(width, 0)
        tmp.rotate(-deg)
        tmp.add(x, y)
        val x2 = tmp.x
        val y2 = tmp.y
        r.color(color.r, color.g, color.b, color.a)
        val v = distance / totalLength
        r.texCoord(0, v)

        r.vertex(x1, y1, 0)

        r.color(color.r, color.g, color.b, color.a)
        r.texCoord(1, v)
        r.vertex(x2, y2, 0)

        i += 2
      }
      r.end()
    }
  }
}
