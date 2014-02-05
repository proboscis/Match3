package com.glyph._scala.lib.util

/**
 * @author glyph
 */
object ArrayOps {
  implicit class WrappedGlyphArray[T](val ary: Array[T]) extends AnyVal {
    def copyStride(length: Int, dst: Array[T], stride: Int, width: Int): Int = {
      var i = 0
      var oi = 0
      var di = 0
      while (i < length) {
        oi = 0
        var pos = i
        while (oi < width && pos < length) {
          dst(di) = ary(pos)
          oi += 1
          di += 1
          pos += 1
        }
        i += stride
      }
      di
    }
  }

}
