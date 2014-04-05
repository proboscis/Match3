package com.glyph._scala.lib.util

/**
 * @author glyph
 */
trait ProfilingOps extends Logging{
  def time(tag:String)(block: =>Unit){
    val start = System.nanoTime()
    block
    val end = System.nanoTime()
    val elapsed = end-start
    val unit = Array("nanos","micro","milli")
    var time = elapsed
    var count = 0
    while(time > 1000){
      time /= 1000
      count += 1
    }
    log("elapsedTime: "+time+unit.lift(count).getOrElse("")+" seconds")
  }
}
