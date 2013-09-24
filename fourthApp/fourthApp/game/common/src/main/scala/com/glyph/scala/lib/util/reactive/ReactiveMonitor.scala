package com.glyph.scala.lib.util.reactive

import ref.WeakReference

/**
 * @author glyph
 */
class ReactiveMonitor {
  println("ReactiveMonitor enabled:5000")
  new Thread(new Runnable(){
    var previous = Var.allVariables
    def run() {
      while(true){
        Thread.sleep(5000)
        /*
        Var.allVariables foreach{
          case WeakReference(ref)=>println(ref)
          case _ => println("lost ref!")
        }*/
        Var.allVariables diff previous foreach{
          case WeakReference(ref)=> println(ref)
          case _ => println("lost ref")
        }
        previous = Var.allVariables
      }
    }
  }).start()
}
