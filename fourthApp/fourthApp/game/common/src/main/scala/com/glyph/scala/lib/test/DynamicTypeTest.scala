package com.glyph.scala.lib.test

import com.glyph.scala.Glyph
import com.glyph.scala.lib.entity_game_system.GameContext
import com.glyph.scala.lib.engine.{Interface, EntityPackage}
import com.glyph.scala.lib.math.Vec2

/**
 * @author glyph
 */
class DynamicTypeTest {
  val TAG = "DynamicTypeTest"
  val time = Glyph.logTime() _
  val game = new GameContext

  val pkg = new EntityPackage("test")
  val entity = pkg.obtain()
  (1 to 999).map{_=>pkg.obtain()}.foreach{_.free()}

  val iTransform = pkg.getMemberIndex[Transform]
  val iTag = pkg.getMemberIndex[Tag]
  val iId = pkg.getMemberIndex[Id]
  val iUpdate = pkg.getInterfaceIndex[Update]
  time("test") {
    var i =0
    while (i < 100){
      val e = pkg.obtain()
      e.setMemberI(iTransform,new Transform)
      e.setMemberI(iTag,new Tag(""+i))
      e.setMemberI(iId,new Id)
      e.setInterfaceI(iUpdate,new Update)
      e.getInterfaceI[Update](iUpdate).update(0.01f)
      i+=1
    }
  }
}

class Transform{
  val position = new Vec2
  val direction = new Vec2
  val acceleration = new Vec2
}
class Tag(val tag:String)
class Id(){
  val id = Id.nextId
}
object Id{
  var currentId=0;
  def nextId :Int= {
    currentId+=1
    currentId - 1
  }
}
class Update extends Interface{
  lazy val transform = owner.getMember[Transform]
  lazy val id = owner.getMember[Id]
  def update(delta:Float){
    println("update!")
    transform.position
  }
}
