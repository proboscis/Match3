package com.glyph.scala.lib.entity_property_system.test

import com.glyph.scala.Glyph
import com.glyph.scala.lib.entity_property_system.World

/**
 * @author glyph
 */
class Test {
  val world = new World

  Glyph.printExecTime("TestGameEngine",{
    var i = 0
    while(i < 1000){
     // Glyph.log(i+"")
     // Glyph.log("createEntity")
      val e = world.entityManager.createEntity()
     // Glyph.log("addComponent")
      e.addComponent(world.poolManager.getPool[TestComponent].obtain())
     // Glyph.log("deleteComponent")
      world.entityManager.deleteEntity(e)
      i += 1

    }
  })

  Glyph.printExecTime("TestGameEngine:Second",{
    var i = 0
    val pool = world.poolManager.getPool[TestComponent]
    while(i < 1000){
     // Glyph.log(i+"")
      val e = world.entityManager.createEntity()
      e.addComponent(pool.obtain())
      //world.entityManager.deleteEntity(e)
      i += 1
    }
  })


  def update(){

  }
}
