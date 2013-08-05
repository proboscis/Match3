package com.glyph.scala

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle


/**
 * @author glyph
 */
package object game {

  def log(tag: String)(msg: String) {
    Glyph.deprecatedLog(tag, msg)
  }

  def time(proc: => Unit): Long = {
    Glyph.deprecatedLog2("=> start")
    val prev = System.nanoTime();
    proc
    val time = (System.nanoTime() - prev)
    time
  }

  def timeToStr(time: Long): String = {
    if (time >= 10000000) {
      "<= " + time / 1000 / 1000 + " millis"
    } else if (time >= 10000) {
      "<= " + time / 1000 + " nanos"
    } else {
      "<= " + time + " micros"
    }
  }

  def printTime(tag: String)(time: Long) {

  }

  /**
   * use this method instead of for loop
   * @param n
   * @param f
   */
  def loop(n: Int, f: (Int) => Unit): Unit = {
    var i = 0
    while (i < n) {
      f(i)
      i = i + 1
    }
  }
}
