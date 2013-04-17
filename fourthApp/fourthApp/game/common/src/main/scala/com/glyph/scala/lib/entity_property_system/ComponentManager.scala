package com.glyph.scala.lib.entity_property_system
import com.glyph.scala.lib.util.Indexer

/**
 * @author glyph
 */
class ComponentManager {
  val components = new Bag[Bag[Component]](64)
  val componentToIndex = collection.mutable.HashMap.empty[Manifest[_<:Component],Int]
  val componentIndexer = new Indexer(64)



  def getComponentIndex[T<:Component](implicit typ: Manifest[T]):Int ={
    componentToIndex get typ match {
      case Some(cIndex)   => cIndex
      case None           => componentIndexer.getNext()
    }
  }

  def getComponent[T<:Component](e:Entity)(implicit typ:Manifest[T]):T={
    val cIndex = getComponentIndex[T]
    getComponent[T](e,cIndex)
  }

  def getComponent[T<:Component](e:Entity,cIndex:Int):T={
    val componentBag = components.get(cIndex)
    if (componentBag != null){
      componentBag.get(e.index).asInstanceOf[T]
    }else{
      null.asInstanceOf[T]
    }
  }

  def addComponent(e:Entity,cIndex:Int,c:Component){
    var componentBag = components.get(cIndex)
    if (componentBag == null){
      components.set(cIndex,new Bag[Component](64))
      componentBag = components.get(cIndex)
    }
    componentBag.set(e.index,c)
  }

  def addComponent[T<:Component](e:Entity,c:Component)(implicit typ: Manifest[T]){
    val cIndex = getComponentIndex[T]
    addComponent(e,cIndex,c)
  }
  def removeComponent[T<:Component](e:Entity,c:Component)(implicit typ: Manifest[T]){
    val cIndex = getComponentIndex[T]
    addComponent(e,cIndex,c)
  }
  def removeComponent(e:Entity,cIndex:Int, c:Component){
    components.get(cIndex).set(e.index,null)
  }

  def removeAllComponent(e:Entity){
    var cIndex = 0
    while(cIndex < components.size){
      val componentBag = components.get(cIndex)
      if (componentBag != null){
        componentBag.set(e.index,null)
      }
      cIndex += 1
    }
  }

  def notifyChanged(e:Entity){
    var cIndex = 0
    while(cIndex < components.size){
      val componentBag = components.get(cIndex)
      if (componentBag != null){
        val component = componentBag.get(e.index)
        if (component != null){
          component.onOwnerStatusChanged(e)
        }
      }
      cIndex += 1
    }
  }
}
