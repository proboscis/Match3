package com.glyph.scala.lib.entity_property_system.test

import com.glyph.scala.Glyph
import com.glyph.scala.lib.entity_property_system.{Component, Entity, World}
import com.glyph.libgdx.util.ArrayStack
import collection.mutable.ListBuffer
import com.glyph.scala.lib.math.Vec2
import com.glyph.scala.lib.util.Chainable

/**
 * @author glyph
 */
class Test {
  val world = new World
  val q = new ArrayStack[Int]
  val eq = new ArrayStack[Entity]
  val printMemory = Glyph.printMemoryDiff("Test") _
  printMemory {}
  printMemory {
    Glyph.printExecTime("TestGameEngine: create thousand entity(1)", {
      val c1pool = world.entityFactory.componentManager.getComponentPool[C1]
      val c2pool = world.entityFactory.componentManager.getComponentPool[C2]
      val c3pool = world.entityFactory.componentManager.getComponentPool[C3]
      var i = 0
      val addList = ListBuffer.empty[Entity]
      printMemory {
        while (i < 1000) {
          val e = world.entityFactory.createEntity().modify {
            e =>
              e.addComponent(c1pool.obtain())
              e.addComponent(c2pool.obtain())
              e.addComponent(c3pool.obtain())
          }
          world.addEntity(e)
          addList += e
          i += 1
        }
      }
      addList.foreach {
        e => world.removeEntity(e); e.delete()
      }
      addList.clear()
    })
  }

  printMemory {
    Glyph.printExecTime("TestGameEngine:create thousand entity(2)", {
      var i = 0
      //val pool = world.poolManager.getPool[TestComponent]
      val factory = world.entityFactory
      val c1pool = world.entityFactory.componentManager.getComponentPool[C1]
      val c2pool = world.entityFactory.componentManager.getComponentPool[C2]
      val c3pool = world.entityFactory.componentManager.getComponentPool[C3]

      val c1Index = world.entityFactory.componentManager.getComponentIndex[C1]
      val c2Index = world.entityFactory.componentManager.getComponentIndex[C2]
      val c3Index = world.entityFactory.componentManager.getComponentIndex[C3]

      printMemory {
        while (i < 1000) {
          // Glyph.log(i+"")
          val e = factory.createEntity()
          
              e.addComponent(c1pool.obtain(), c1Index)
              e.addComponent(c2pool.obtain(), c2Index)
              e.addComponent(c3pool.obtain(), c3Index)

          e.notifyChanged()
          i += 1
        }
      }
    })
  }
  Glyph.printExecTime("TestGameEngine:linkedList add 1000 dummy", {
    new Test2()
  })

  val queue = collection.mutable.Queue.empty[Int]
  for (i <- 0 to 1000) {
    queue.enqueue(i)
  }
  Glyph.printExecTime("TestGameEngine:queue test", {
    while (!queue.isEmpty) {
      queue.dequeue()
    }
  })

  Test3.test()
  Glyph.printExecTime("TestGameEngine:java 1000 method", {
    var i = 0;
    while (i < 1000) {
      i += 1
      Test3.test()
    }
  })

  val test4 = new Test4
  Glyph.printExecTime("TestGameEngine:linkedlist pop", {
    test4.test()
  })


  def update() {

  }

}

class C1 extends Component with Chainable{
  val v = Vec2
}

class C2 extends Component {
  val a = Vec2
}

class C3 extends Component {
  val c = Vec2
}