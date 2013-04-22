package com.glyph.scala.lib.entity_property_system.test

import com.glyph.scala.Glyph
import com.glyph.scala.lib.entity_property_system.{GameSystem, Component, Entity, World}
import com.glyph.libgdx.util.ArrayStack
import collection.mutable.ListBuffer
import com.glyph.scala.lib.math.Vec2
import com.glyph.scala.lib.util.table.DataTable

/**
 * @author glyph
 */
class Test {
  val world = new World
  world.systemManager.registerSystem(new S1)
  world.systemManager.registerSystem(new S2)
  world.systemManager.registerSystem(new S3)
  world.systemManager.registerSystem(new S4)
  world.systemManager.registerSystem(new S5)
  world.systemManager.registerSystem(new S6)
  val q = new ArrayStack[Int]
  val eq = new ArrayStack[Entity]
  val printMemory = Glyph.printMemoryDiff("Test") _
  printMemory {}
  printMemory {
    Glyph.printExecTime("TestGameEngine: create thousand entity(1)", {
      val c1pool = world.componentPool[C1]
      val c2pool = world.componentPool[C2]
      val c3pool = world.componentPool[C3]
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
      val c1pool = world.componentPool[C1]
      val c2pool = world.componentPool[C2]
      val c3pool = world.componentPool[C3]

      val c1Index = world.componentIndex[C1]
      val c2Index = world.componentIndex[C2]
      val c3Index = world.componentIndex[C3]

      printMemory {
        while (i < 1000) {
          // Glyph.log(i+"")
          val e = factory.createEntity()
          e.addComponent(c1pool.obtain(), c1Index)
          e.addComponent(c2pool.obtain(), c2Index)
          e.addComponent(c3pool.obtain(), c3Index)
          e.notifyChanged()
          world.addEntity(e)
          i += 1
        }
      }
    })
    Glyph.printExecTime("while 1000", {
      var i = 0;
      while (i < 1000) {
        i += 1
      }
    })
    Glyph.log("entities:"+world.entities.size())
    Glyph.printExecTime("arrayStack 1000", {
      val entities = world.entities
      val size = entities.size()
      var i = 0;
      while (i < size) {
        val e = entities.get(i)
        i += 1
      }
    })
    Glyph.printExecTime("test update",{
      world.updateSystems(0.016f)
    })
  }



  def update() {

  }
}

class S4 extends S2
class S5 extends S3
class S6 extends S1
class S1 extends GameSystem(manifest[C1], manifest[C2]){
  override def update(delta: Float) {
    super.update(delta)
    Glyph.log("entities to process:"+entities.size())
    val mapper = world.componentManager.getComponentMapper[C1]
    var i =0
    while ( i < entities.size()){
      i+=1
    }
  }
}
class S2 extends GameSystem(manifest[C2], manifest[C3]){
  override def update(delta: Float) {
    Glyph.log("entities to process:"+entities.size())
    super.update(delta)
    val mapper = world.componentManager.getComponentMapper[C2]
    var i = 0;
    while ( i < entities.size()){
      val c2 = mapper.get(entities.get(i))
      i+= 1
    }
  }
}
class S3 extends GameSystem(manifest[C3], manifest[C1]){
  override def update(delta: Float) {
    Glyph.log("entities to process:"+entities.size())
    super.update(delta)
    val mapper = world.componentManager.getComponentMapper[C3]
    var i = 0;
    while ( i < entities.size()){
      mapper.get(entities.get(i))
      i+= 1
    }
  }
}

class C1 extends Component {
  val v = Vec2
}

class C2 extends Component {
  val a = Vec2
}

class C3 extends Component {
  val c = Vec2
}