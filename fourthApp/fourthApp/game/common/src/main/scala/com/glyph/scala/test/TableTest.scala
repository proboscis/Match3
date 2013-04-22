package com.glyph.scala.test

import com.glyph.scala.Glyph
import com.glyph.scala.lib.util.table.DataTable
import com.glyph.scala.lib.math.Vec2
import java.util
import com.glyph.libgdx.util.ArrayStack
import com.glyph.libgdx.util.LinkedList


/**
 * @author glyph
 */
class TableTest {
  def log = Glyph.log("TableTest", _: String)

  /*
  val table = new DataTable
  val positions = table.addColumn[Vec2]("position")
  val directions = table.addColumn[Vec2]("direction")
  table.set(1000,"position",new Vec2);
  */
  val map = new util.HashMap[Manifest[_], Any]()
  Glyph.printExecTime("while 1000", {
    var i = 0;
    while (i < 1000) {
      i += 1
    }
  })
  Glyph.printExecTime("hash put", {
    var i = 0
    while (i < 1000){
      map.put(manifest[TableTest], i)

      i+=1
    }
  })
  val man = manifest[TableTest]
  Glyph.printExecTime("manifest hashCode", {
    var i = 0
    while (i < 1000){
      man.hashCode
      i+=1
    }
  })
  Glyph.printExecTime("int hashCode", {
    var i = 0
    while (i < 1000){
      (i).hashCode
      i+=1
    }
  })
  Glyph.printExecTime("hash get", {
    var i = 0;
    while (i < 1000) {
      map.get(man)
      i += 1
    }
  })
  val array = new ArrayStack[Int]
  Glyph.printExecTime("array push", {
    var i = 0
    while (i < 1000){
      array.push(i)
      i+=1
    }
  })
  Glyph.printExecTime("array get", {
    var i = 0
    while (i < 1000){
      val t = array.get(i)
      i+=1
    }
  })
  val list = new LinkedList[Int]
  Glyph.printExecTime("list push", {
    var i = 0
    while (i < 1000){
      list.push(i)
      i+=1
    }
  })
  Glyph.printExecTime("list get", {
    var next = list.head
    while(next.next != null){
      next = next.next
    }
  })
  val it = list.iterator()
  Glyph.printExecTime("list iteration", {
    while(it.hasNext){
      it.next()
    }
  })
  val javalist = new java.util.LinkedList[Int]
  Glyph.printExecTime("javalist push", {
    var i = 0
    while (i < 1000){
      javalist.push(i)
      i+=1
    }
  })
  val it2 = javalist.iterator()
  Glyph.printExecTime("javalist iteration", {
    while(it2.hasNext){
      it2.next()
    }
  })
}
