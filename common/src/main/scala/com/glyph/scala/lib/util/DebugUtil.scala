package com.glyph.scala.lib.util

/**
 * @author glyph
 */
object DebugUtil {
  def logTime(block: => Unit)={
    val prev = System.nanoTime()
    block
    System.nanoTime() - prev
  }
  def log(logger:String=>Unit)(msg: String)(f: => Unit){
    logger(msg + " =>")
    logger(msg + "<=: " + LongToTimeStr(logTime {
      f
    }))
  }

  implicit def LongToTimeStr(time: Long): String = time match {
    case t if t >= 10000000 => time / 1000 / 1000 + " millis"
    case t if (t >= 10000) => time / 1000 + " nanos"
    case t => time + " micros"
  }
}
