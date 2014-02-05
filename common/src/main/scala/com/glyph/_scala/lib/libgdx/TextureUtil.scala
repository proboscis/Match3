package com.glyph._scala.lib.libgdx

import com.badlogic.gdx.graphics.{Texture, Color, Pixmap}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Sprite
import com.glyph._scala.lib.util.pool.Pool
import scala.collection.mutable

/**
 * @author glyph
 */
object TextureUtil {

  private val image = new Pixmap(128, 128, Pixmap.Format.RGBA8888)
  image.setColor(Color.WHITE)
  image.fillRectangle(0, 0, image.getWidth, image.getHeight)
  private val texture = new Texture(image)

  def dummy(assets: AssetManager) = assets.get[Texture]("data/dummy.png") //texture

  def split(sprite: Sprite)(divX: Float)(divY: Float)(dst:mutable.Buffer[Sprite])(implicit pool: Pool[Sprite]) {
    var u, v, u2, v2 = 0f
    val spx = sprite.getX
    val spy = sprite.getY
    val sx = sprite.getScaleX
    val sy = sprite.getScaleY
    val rx = sprite.getRegionX
    val ry = sprite.getRegionY
    val rw: Float = sprite.getRegionWidth
    val rh: Float = sprite.getRegionHeight
    val tw: Float = sprite.getTexture.getWidth
    val th: Float = sprite.getTexture.getHeight

    val width = sprite.getWidth * sx
    val height = sprite.getHeight * sy
    val nX = width / divX
    val nY = height / divY
    val texDivX: Float = rw / tw / nX
    val texDivY: Float = rh / th / nY
    var x = 0
    while (x < nX) {
      var y = 0
      while (y < nY) {
        val sp = pool.manual
        u = rx + texDivX * x
        v = ry + texDivY * y
        u2 = u + texDivX
        v2 = v + texDivY
        sp.setTexture(sprite.getTexture)
        sp.setRegion(u, v, u2, v2)
        sp.setOrigin(divX / 2, divY / 2)
        sp.setSize(divX, divY)
        sp.setPosition(spx + x * divX * sx, spy + height - y * divY * sy)
        sp.setColor(sprite.getColor)
        dst += sp
        y += 1
      }
      x += 1
    }
  }
}
