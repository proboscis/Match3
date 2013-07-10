package com.glyph.test

import com.glyph.scala.Glyph
import collection.mutable.ListBuffer
import collection.immutable.Queue

/**
 * @author glyph
 */
object PFTest {
  def main(args: Array[String]) {
    exec("seq"){
      var seq = Seq[Int]()
      var i = 0
      while (i < 10000){
        seq = seq.+:(i)
        i+=1
      }
      //println(seq)
    }
    exec("listbuf"){
      val buf = ListBuffer[Int]()
      var i = 0
      while(i < 10000){
        buf += i
        i += 1
      }
      //println(buf)
    }
    exec("vec"){
      var vec = Vector[Int]()
      var i = 0
      while(i < 10000){
        vec = vec.+:(i)
        i += 1
      }
    }
    exec("list"){
      var list = List[Int]()
      var i = 0
      while(i < 10000){
        list = i :: list
        i += 1
      }
      //println(list)
    }
    exec("queue"){
      var queue = Queue[Int]()
      var i = 0
      while ( i < 10000){
        queue = queue.enqueue(i)
        i += 1
      }
     // println(queue)
    }
    exec("stack"){
      var stack = collection.immutable.Stack[Int]()
      var i = 0
      while(i < 10000){
        stack = stack.push(i)
        i += 1
      }
      //println(stack)
    }

  }
  def exec(tag:String)(f: =>Unit){
    print(tag,"start=>...")
    val prev = System.nanoTime()
    f
    val time = System.nanoTime() - prev
    printTime(tag,time)
  }
  def printTime(tag: String, time: Long) {
    if (time >= 10000000) {
      println(tag, "<= " + time / 1000 / 1000 + " millis")
    } else if (time >= 10000) {
      println(tag, "<= " + time / 1000 + " nanos");
    } else {
      println(tag, "<= " + time + " micros");
    }
  }
}
