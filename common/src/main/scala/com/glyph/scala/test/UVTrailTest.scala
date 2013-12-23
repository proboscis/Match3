package com.glyph.scala.test

import com.badlogic.gdx.graphics._
import com.glyph.scala.lib.libgdx.gl.ShaderHandler
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.{Screen, Gdx}
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.game.Glyphs
import Glyphs._
/**
 * @author glyph
 */
class UVTrailTest extends ScreenBuilder {
  def requiredAssets: Set[(Class[_], Seq[String])] = Set(classOf[Texture] -> ("data/particle.png" :: Nil))

  def create(assetManager: AssetManager): Screen = {
    implicit val am = assetManager
    val texture:Texture = "data/particle.png".fromAssets
    new AppliedTrailTest(
    1000,
    new BaseStripBatch(1000 * 10 * 2, UVTrail.ATTRIBUTES),
    ShaderHandler("shader/rotate2.vert", "shader/default.frag"),
    () => new UVTrail(10),
      s =>texture.bind(),
    true
  )
  }
}
class UVTrail(size: Int) extends BaseTrail(size) {
  def vertexSize: Int = UVTrail.VERTEX_SIZE

  def setupMesh() {
    val color = Color.WHITE.toFloatBits
    val recordLength = count / 2 //records.length/2
    var i = 0
    val v = meshVertices
    val VERTEX_SIZE = vertexSize
    var vi = VERTEX_SIZE * 2
    while (i < recordLength - 1) {
      //for all vertices
      val ri = i * 2
      val x = records(ri)
      val y = records(ri + 1)
      val nx = records(ri + 2)
      val ny = records(ri + 3)
      v(vi) = x
      v(vi + 1) = y
      v(vi + 2) = color
      v(vi + 3) = nx - x
      v(vi + 4) = ny - y
      v(vi + 5) = -1
      v(vi + 6) = i / MAX.toFloat
      if (i == 0) {
        v(0) = x
        v(1) = y
        v(2) = color
        v(3) = nx - x
        v(4) = ny - y //u
        v(5) = -1 //v
        v(6) = 0
      }
      vi += VERTEX_SIZE
      v(vi) = x
      v(vi + 1) = y
      v(vi + 2) = color
      v(vi + 3) = nx - x
      v(vi + 4) = ny - y
      v(vi + 5) = 1
      v(vi + 6) = i / MAX.toFloat
      if (i == 0) {
        v(VERTEX_SIZE) = x
        v(VERTEX_SIZE + 1) = y
        v(VERTEX_SIZE + 2) = color
        v(VERTEX_SIZE + 3) = nx - x
        v(VERTEX_SIZE + 4) = ny - y
        v(VERTEX_SIZE + 5) = 1
        v(VERTEX_SIZE + 6) = 0
      }
      vi += VERTEX_SIZE
      i += 1
    }
  }
}

object UVTrail {
  val VERTEX_SIZE = 2 + 1 + 2 + 1 + 1
  val WIDTH_ATTRIBUTE_ALIAS = "a_width"
  val LENGTH_ATTRIBUTE_ALIAS = "a_length"
  val LENGTH_ATTRIBUTE = new VertexAttribute(Usage.Generic, 1, LENGTH_ATTRIBUTE_ALIAS)
  val WIDTH_ATTRIBUTE = new VertexAttribute(Usage.Generic, 1, WIDTH_ATTRIBUTE_ALIAS)
  val NORMAL_ATTRIBUTE = new VertexAttribute(Usage.Normal, 2, ShaderProgram.NORMAL_ATTRIBUTE)
  val POSITION_ATTRIBUTE_2D = new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE)
  val ATTRIBUTES = new VertexAttributes(
    POSITION_ATTRIBUTE_2D,
    VertexAttribute.Color(),
    NORMAL_ATTRIBUTE,
    WIDTH_ATTRIBUTE,
    LENGTH_ATTRIBUTE)
}