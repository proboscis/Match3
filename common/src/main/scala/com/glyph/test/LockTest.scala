package com.glyph.test

import com.glyph.scala.lib.util.observer.{Observing, Observable}

/**
 * @author glyph
 */
object LockTest extends Observing{
  def main(args: Array[String]) {
    val model = new Model
    observe(model.lock){
      case true=>println("locked")
      case false=>println("released")
    }
    model.lock {
      a => println(a.data)
    }
  }
  class Model{
    val lockQueue = collection.mutable.Queue[(Accessor)=>Unit]()
    var locked = false
    val lock = new Observable[Boolean]
    private val mData = "abcde"
    class Accessor{
      def data = mData
      def release(){
        locked = false
      }
    }
    def lock(f:(Accessor)=>Unit){
      if (!locked){
        locked = true
        lock(true)
        f(new Accessor)
        locked = false
        lock(false)
      }else{
        lockQueue.enqueue(f)
      }
    }
  }
  trait Lockable{
  }
}
