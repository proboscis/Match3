package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.{Gdx, Screen}
import com.glyph.scala.lib.libgdx.gl.{GlyphFrameBuffer, FloatTexture, ShaderHandler}
import com.badlogic.gdx.graphics.glutils.{ShaderProgram, ImmediateModeRenderer, ImmediateModeRenderer20}
import com.badlogic.gdx.graphics._
import com.glyph.scala.game.Glyphs
import Glyphs._
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.glyph.scala.lib.util.Logging
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.utils.BufferUtils
import java.nio.FloatBuffer
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.{Interpolation, MathUtils, Vector2}
import scalaz._
import Scalaz._

/**
 * @author glyph
 */
class TransformFeedback extends ScreenBuilder with Logging {
  def requirements: Set[(Class[_], Seq[String])] = Set(
    classOf[Texture] -> Seq("data/sword.png", "data/dummy.png", "data/particle.png")
  )

  def create(implicit assetManager: AssetManager): Screen = new ConfiguredScreen {
    ShaderProgram.pedantic = false
    println(Gdx.gl.glGetString(GL10.GL_EXTENSIONS))

    override def STAGE_WIDTH: Int = 960

    override def STAGE_HEIGHT: Int = 540

    val pointShaderHandler = new ShaderHandler("shader/point.vert", "shader/point.frag")
    val feedbackShaderHandler = new ShaderHandler("shader/feedback.vert", "shader/feedback.frag")
    val powerShaderHandler = new ShaderHandler("shader/power.vert", "shader/power.frag")
    val pointRenderer = new ImmediateModeRenderer20(1000, false, true, 0)
    val textureRenderer = new ImmediateModeRenderer20(1000, false, true, 1)
    //TODO this format defines whether the texture2D in glsl returns clamped data or not
    val PARTICLE_COUNT_W = 3000
    val PARTICLE_COUNT_H = 3000
    val MAP_RESOLUTION = 200
    val RADIUS = 5
    val frameBuffers = Array(1 to 2 map (_ => new GlyphFrameBuffer(FloatTexture(PARTICLE_COUNT_W, PARTICLE_COUNT_H), false)): _*)
    val powerMap = new GlyphFrameBuffer(FloatTexture(MAP_RESOLUTION, MAP_RESOLUTION), false)
    //TODO extend FrameBuffer to use my own Texture which internally uses FloatBuffer
    //TODO extends Texture to fool the FrameBuffer class
    val a_position = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
    val a_texCoord = VertexAttribute.TexCoords(0)
    val rect = new Mesh(true, 4, 0, a_position, a_texCoord)
    rect.setVertices(rectangle(-0.5f, -0.5f, 1f, 1f))
    val particleVertices = new Mesh(true, PARTICLE_COUNT_W * PARTICLE_COUNT_H, 0, a_position)

    def rectangle(x: Float, y: Float, w: Float, h: Float): Array[Float] = Array[Float](
      x, y, 0, 0,
      x, y + h, 0, 1,
      x + w, y, 1, 0,
      x + w, y + h, 1, 1
    )

    {
      //init particleVertices
      val vertices = (0 until PARTICLE_COUNT_W * PARTICLE_COUNT_H map (i => Array((i / PARTICLE_COUNT_W) / PARTICLE_COUNT_W.toFloat, (i % PARTICLE_COUNT_W) / PARTICLE_COUNT_H.toFloat))).flatten
      particleVertices.setVertices(Array(vertices: _*))
      println(vertices.size)
    }

    class TextureRenderActor(tex: GLTexture) extends Actor {
      override def draw(batch: Batch, parentAlpha: Float): Unit = {
        super.draw(batch, parentAlpha)
        batch.end()
        val tr = textureRenderer
        tr.begin(batch.getProjectionMatrix, GL10.GL_TRIANGLE_STRIP)
        tex.bind()
        texturedRect(tr)(Color.WHITE, getX, getY, getWidth, getHeight)
        tr.end()
        batch.begin()
      }
    }

    val leftTable = new Table
    leftTable.debug()
    val rightTable = new Table
    rightTable.debug()
    val texture: Texture = "data/sword.png".fromAssets
    val dummyTexture: Texture = "data/dummy.png".fromAssets
    val particleTexture: Texture = "data/particle.png".fromAssets
    val forceTexture = createForceFieldTexture(MAP_RESOLUTION)
    val gravityTexture = new FloatTexture(MAP_RESOLUTION, MAP_RESOLUTION, createGravityField(MAP_RESOLUTION))

    import Noise._
    def generateNoise(level:Int) = interpolate2D(Array.fill(level*level)(MathUtils.random(-1f,1f)),level,level)_
    def generateNoises(min:Int,max:Int) = min to max map generateNoise
    val noises = generateNoises(3,16)
    val RESOLUTION = 400
    def funcToTexture(f:(Float,Float)=>Float,width:Int,height:Int) = new FloatTexture(width,height,newFloatBuffer(fill2D(RESOLUTION,RESOLUTION)(f) |> float1DToFloat4D))
    val noiseTextures = noises map(funcToTexture(_,RESOLUTION,RESOLUTION))
    def generatePerlinNoise(noises:Seq[(Float,Float)=>Float]) = (x:Float,y:Float)=> noises.map(_(x,y)).sum/noises.size
    val filledPerlins = 1 to 2 map (_ => generateNoises(3,16)) map generatePerlinNoise map (fill2D(RESOLUTION,RESOLUTION)(_))
    val perlinTexture = new FloatTexture(RESOLUTION,RESOLUTION,newFloatBuffer(floatNDToFloatMD(filledPerlins,4)))
    val textureActors = ((frameBuffers :+ powerMap).map(_.getColorBufferTexture) ++ noiseTextures:+perlinTexture :+ forceTexture :+ gravityTexture).map(new TextureRenderActor(_))
    textureActors foreach {
      actor => rightTable.add(actor).fill.expand.row
    }

    root.add(leftTable).fill.expand(145, 90)
    root.add(rightTable).fill.expand(15, 90)

    var mouseX = 0f
    var mouseY = 0f
    stage.addListener(new InputListener {
      override def mouseMoved(event: InputEvent, x: Float, y: Float): Boolean = {
        super.mouseMoved(event, x, y)
        mouseX = x
        mouseY = y
        true
      }
    })


    val camera = new OrthographicCamera(1, 1)
    camera.update()
    val renderFunction = pointShaderHandler.shader ~ feedbackShaderHandler.shader ~ powerShaderHandler.shader map {
      case pointShaderOpt ~ feedbackShaderOpt ~ powerShaderOpt => {
        var renderFailed = false
        var swap = false
        var init = true
        () => {
          if (!renderFailed && pointShaderOpt.isDefined && feedbackShaderOpt.isDefined) {
            try {
              val gl = Gdx.gl
              import gl._
              import GL20._
              Gdx.gl20.glEnable(GL11.GL_POINT_SPRITE_OES) //this is required!!!
              //// http://stackoverflow.com/questions/13213227/gl-pointcoord-has-incorrect-uninitialized-value
              Gdx.gl.glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
              //log("drawing")
              val pointShader = pointShaderOpt.get
              val feedbackShader = feedbackShaderOpt.get
              val powerShader = powerShaderOpt.get
              val pr = pointRenderer
              val tr = textureRenderer
              camera.update()
              val i1 = if (swap) 0 else 1
              val i2 = if (swap) 1 else 0

              /**
               * 力場をgpuでフレームバッファに加算合成することで作ることができるぞ！
               */
              //TODO viscocity etc..


              /*
              {
                //update power map
                import powerShader._
                begin()
                //stage.getCamera.position.set(0,0,0)
                frameBuffers(i1).getColorBufferTexture.bind(0)
                //forceTexture.bind(1)
                gravityTexture.bind(1)
                setUniformMatrix("u_projModelView", stage.getCamera.combined)
                setUniformi("u_sampler0", 0)
                setUniformi("u_sampler1", 1)
                setUniformf("u_pointSize", MAP_RESOLUTION/RADIUS)
                import GL20._
                glDisable(GL_DEPTH_TEST)
                glEnable(GL_BLEND)
                glBlendFunc(GL_ONE, GL_ONE)
                //Gdx.gl20.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_SRC_ALPHA, GL_ONE)
                powerMap.begin()
                glClear(GL_COLOR_BUFFER_BIT)
                particleVertices.render(powerShader, GL_POINTS)
                powerMap.end()
                end()
              }
              */

              {
                import feedbackShader._
                glDisable(GL_BLEND)
                //ping-ponwg the vel pos texture
                begin()
                frameBuffers(i1).getColorBufferTexture.bind(0) //this costs nothing.
                //powerMap.getColorBufferTexture.bind(1)
                //texture.bind(1)
                //gravityTexture.bind(1)
                //forceTexture.bind(1)
                perlinTexture.bind(1)
                setUniformMatrix("u_projModelView", camera.combined)
                setUniformi("u_sampler0", 0)
                setUniformi("u_sampler1", 1)
                setUniformf("u_dt", 0.016f)
                setUniformi("u_state", 0)
                setUniformi("u_init", if (init) 1 else 0)
                setUniformf("mouse", mouseX, mouseY)
                frameBuffers(i2).begin()
                rect.render(feedbackShader, GL_TRIANGLE_STRIP)
                frameBuffers(i2).end()
                end()
                swap = !swap
                init = false
              }

              {
                // now draw particles with vel_pos texture
                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE)
                pointShader.begin()
                //stage.getCamera.position.set(0,0,0)
                frameBuffers(i2).getColorBufferTexture.bind(0)
                particleTexture.bind(1)
                //forceTexture.bind(1)
                powerMap.getColorBufferTexture.bind(2)
                pointShader.setUniformMatrix("u_projModelView", stage.getCamera.combined)
                pointShader.setUniformi("u_sampler0", 0)
                pointShader.setUniformi("u_sampler1", 1)
                pointShader.setUniformi("u_sampler2", 2)
                pointShader.setUniformf("u_pointSize", 1)
                particleVertices.render(pointShader, GL_POINTS)
                pointShader.end()
              }
            } catch {
              case e: Throwable => e.printStackTrace(); renderFailed = true
            }
          }
        }
      }
    }

    override def render(delta: Float): Unit = {
      super.render(delta)
      renderFunction()()
      Gdx.gl.glActiveTexture(GL10.GL_TEXTURE0) // <= this is required
    }


    def drawRect(r: ImmediateModeRenderer)(color: Color, x: Float, y: Float, w: Float, h: Float) {
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y + h, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + w, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + w, y + h, 0)
    }

    def texturedRect(r: ImmediateModeRenderer)(color: Color, x: Float, y: Float, w: Float, h: Float) {
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(0, 0)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(0, 1)
      r.vertex(x, y + h, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(1, 0)
      r.vertex(x + w, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.texCoord(1, 1)
      r.vertex(x + w, y + h, 0)
    }

    def drawPrim1(r: ImmediateModeRenderer) {
      val color = Color.WHITE
      val x = 5
      val y = 5
      val width = 40
      val height = 40
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x, y + height, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + width, y, 0)
      r.color(color.r, color.g, color.b, color.a)
      r.vertex(x + width, y + height, 0)
    }
  }

  def createForceField(size: Int): FloatBuffer = {
    val center = new Vector2(size / 2, size / 2)
    val tmp = new Vector2()
    val data = new Array[Float](size * size * 4)
    var x = 0
    while (x < size) {
      var y = 0
      while (y < size) {
        val idx = (y * size + x) * 4
        tmp.set(x, y).sub(center)
        val l2 = tmp.len2()
        val amp = if (l2 > size / 2 * size / 2 || l2 < 0.0000001) 0 else 1f / l2
        tmp.nor()
        data(idx) = tmp.x * amp
        data(idx + 1) = tmp.y * amp
        data(idx + 2) = amp
        data(idx + 3) = 1f
        y += 1
      }
      x += 1
    }
    newFloatBuffer(data)
  }

  def createGravityField(size: Int): FloatBuffer = {
    val center = new Vector2(size / 2, size / 2)
    val tmp = new Vector2()
    val data = new Array[Float](size * size * 4)
    var x = 0
    while (x < size) {
      var y = 0
      while (y < size) {
        val idx = (y * size + x) * 4
        tmp.set(x, y).sub(center).scl(-1)
        val l2 = tmp.len2()
        val amp = if (l2 > size * size / 4) 0 else l2
        tmp.nor()
        data(idx) = tmp.x * amp
        data(idx + 1) = tmp.y * amp
        data(idx + 2) = amp
        data(idx + 3) = 1f
        y += 1
      }
      x += 1
    }
    newFloatBuffer(data)
  }

  def newFloatBuffer(data: Array[Float]): FloatBuffer = {
    val result = BufferUtils.newFloatBuffer(data.length)
    result.put(data)
    result.flip()
    result
  }


  def createForceFieldTexture(size: Int): FloatTexture = new FloatTexture(size, size, createForceField(size))
}

object Noise{

  def randomValues(n: Int): Array[Float] = Array.fill(n)(MathUtils.random(-1f, 1f))

  def bilinear(a: Float, b: Float, c: Float, d: Float, ax: Float, ay: Float): Float = {
    val _ax = 1f - ax
    val _ay = 1f - ay
    _ax * (a * _ay + b * ay) + ax * (c * _ay + d * ay)
  }

  def interpolate2D(vertices: IndexedSeq[Float], nx: Int, ny: Int)(ax: Float, ay: Float): Float = {
    import MathUtils._
    val dx = 1f / (nx - 1).toFloat
    val dy = 1f / (ny - 1).toFloat
    val ix = (clamp(ax, 0, 1) / dx).toInt
    val iy = (clamp(ay, 0, 1) / dy).toInt
    val nix = clamp(ix + 1, 0, nx - 1)
    val niy = clamp(iy + 1, 0, ny - 1)
    bilinear(vertices(iy * nx + ix),vertices(niy * nx + ix),vertices(iy * nx + nix),vertices(niy * nx + nix),(ax%dx)/dx,(ay%dy)/dy)
  }
  def nop2D(vertices: IndexedSeq[Float], nx: Int, ny: Int)(ax: Float, ay: Float): Float = {
    import MathUtils._
    val dx = 1f / (nx - 1).toFloat
    val dy = 1f / (ny - 1).toFloat
    val ix = (clamp(ax, 0, 1) / dx).toInt
    val iy = (clamp(ay, 0, 1) / dy).toInt
    val ia = iy * nx + ix
    val a = vertices(ia)
    a
  }
  def random2D(width: Int, height: Int, level: Int)= fill2D(width, height)(
    interpolate2D(randomValues(level * level), level, level)
  )

  def fill2D(width: Int, height: Int)(values: (Float, Float) => Float): Array[Float] = {
    val result = new Array[Float](width * height)
    var x = 0
    val wf = width.toFloat
    val hf = height.toFloat
    while (x < width) {
      var y = 0
      while (y < height) {
        // println(x/wf,y/wf)
        result(y * width + x) = values(x.toFloat / wf, y.toFloat / hf)
        y += 1
      }
      x += 1
    }
    result
  }

  def float1DToFloat4D(data: Array[Float]): Array[Float] = {
    val result = new Array[Float](data.length * 4)
    var i = 0
    val l = data.length
    while (i < l) {
      val ri = i * 4
      result(ri) = data(i)
      result(ri + 1) = 0
      result(ri + 2) = 0
      result(ri + 3) = 0
      i += 1
    }
    result
  }
  def floatNDToFloatMD(dataSet:IndexedSeq[Array[Float]],dimension:Int):Array[Float]={
    val dataDimension = dataSet.length
    val result = new Array[Float](dataSet(0).length*dimension)
    val l = dataSet(0).length
    var i = 0
    while (i < l) {
      var y = 0
      while(y < dimension){
        result(i * dimension+y) = if(y < dataDimension) dataSet(y)(i) else 0
        y += 1
      }
      i += 1
    }
    result
  }
}





