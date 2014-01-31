package com.glyph.scala.lib.libgdx
import scala.collection.{mutable => m}
import com.badlogic.gdx.Screen
import com.glyph.scala.lib.libgdx
import com.badlogic.gdx.assets.AssetManager
import com.glyph.scala.lib.libgdx.screen.ConfiguredScreen

class SceneGraph(nodes:Seq[Builder[Screen]]){
  val stack:m.Stack[Screen] = m.Stack()
  def push(b:Builder[Screen])= ???
  def pop() = ???
  class A{
    val a: ()=>Unit = ???
    val b: Int=>Unit = ???
    val c: Double => Unit = ???
  }
  val a = new A
  /**
   * what matters is how you connect them.
   * it requires attaching methods
   */
  val set = (a,Seq(((_:A).a,()=>{}),(a.b,(_:Int)*2)))
  val screenA :Builder[()=>Screen] = Builder(Set(),_=>()=>new ConfiguredScreen{})
  val screenB:Builder[Int=>Screen] = Builder(Set(),_=>(x:Int)=>new ConfiguredScreen{})
  
}

object SceneGraph {
}