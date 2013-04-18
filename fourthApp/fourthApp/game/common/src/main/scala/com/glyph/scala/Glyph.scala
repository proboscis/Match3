package com.glyph.scala

import com.badlogic.gdx.Gdx


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

  def memoryDiff(func: => Unit) = {
    val prevMem:Long =  allocation()
    func
    val currMem:Long = allocation()
    currMem - prevMem
  }:Long

  def printMemoryDiff(tag:String)(f: => Unit){
    log(tag+":memory diff",""+memoryDiff{
      f
    })
    log(tag+":memory",""+allocation());
  }

  def allocation():Long = {
    Runtime.getRuntime.totalMemory()-Runtime.getRuntime.freeMemory()
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

  /**
   * use this method instead of for loop
   * @param n
   * @param f
   */
  def loop(n:Int,f:Int=>Unit):Unit={
    var i = 0
    while(i < n){
      f(i)
      i = i+1
    }
  }
}
