package com.glyph.scala

import com.badlogic.gdx.Gdx

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/07
 * Time: 0:40
 * To change this template use File | Settings | File Templates.
 */
object Glyph {
  val TAG = "com.glyph:"

  def log(str: String) = {
    Gdx.app.log(TAG, str)
  }

  def log(tag: String, str: String) = {
    Gdx.app.log(TAG + tag, str)
  }

  def printExecTime(tag: String, func: => Unit) {
    Glyph.log(tag, "=> start")
    val prev = System.nanoTime();
    func
    val time = (System.nanoTime() - prev)
    printTime(tag, time);
  }

  def printExecTime(func: => Unit) {
    Glyph.log("=> start")
    val prev = System.nanoTime();
    func
    val time = (System.nanoTime() - prev)
    printTime("", time)
  }

  def printTime(tag: String, time: Long) {
    if (time >= 10000000) {
      Glyph.log(tag, "<= " + time / 1000 / 1000 + "millis");
    } else if (time >= 10000) {
      Glyph.log(tag, "<= " + time / 1000 + "nanos");
    } else {
      Glyph.log(tag, "<= " + time + "micros");
    }
  }

  def execTime(func: => Unit): Long = {
    val prev = System.nanoTime();
    func
    (System.nanoTime() - prev)
  }

  /**
   * interval in millis
   * @param interval
   */
  class Timer(interval: Long) {
    var timer = interval * 1000 * 1000
    var prevTime: Long = System.nanoTime()
    var currentTime: Long = System.nanoTime()

    def repeat(func: => Unit) {
      prevTime = currentTime
      currentTime = System.nanoTime()
      timer -= currentTime - prevTime
      if (timer <= 0) {
        func
        timer = interval*1000*1000
      }
    }
    
  }

}
