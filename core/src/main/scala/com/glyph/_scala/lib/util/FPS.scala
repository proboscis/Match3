package com.glyph._scala.lib.util

/**
 * @author glyph
 */
class FPS {
  private val log = new Array[Float](60)
  private var index = 0
  private var mFps = 0f

  def update(delta: Float) {
    log(index) = delta
    index += 1
    val len = log.length
    index = index % len
    var i = 0
    var sum = 0f
    while (i < len) {
      sum += log(i)
      i += 1
    }
    sum /= 60 //average delta
    mFps = 1f / delta
  }

  def fps = mFps
}
