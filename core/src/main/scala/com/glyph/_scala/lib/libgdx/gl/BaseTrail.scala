package com.glyph._scala.lib.libgdx.gl

import scala.Float
import com.glyph._scala.lib.util.Logging


trait Trail{
  def addTrail(x:Float,y:Float)
  def reset()
}

/**
 * @author glyph
 */
abstract class BaseTrail(val MAX: Int) extends Logging with Trail{
  def vertexSize: Int

  //CAUTION !!! when you access members a lot, create a local variable to hold that reference.
  //so that it can avoid method invocation
  val records = new Array[Float](MAX * 2)
  val meshVertices = new Array[Float](MAX * 2 * vertexSize)
  var count = 0
  def addTrail(x: Float, y: Float) {
    val _records = records
    val _count = count
    val l = _records.length

    if (_count >= l) {
      System.arraycopy(_records, 2, _records, 0, l - 2)
      _records(l - 2) = x
      _records(l - 1) = y
      setupMesh()
    } else {
      _records(_count) = x
      _records(_count + 1) = y
      count += 2
      setupMesh()
    }
  }

  def setupMesh()

  def reset() {
    java.util.Arrays.fill(records, 0)
    count = 0
  }
}
