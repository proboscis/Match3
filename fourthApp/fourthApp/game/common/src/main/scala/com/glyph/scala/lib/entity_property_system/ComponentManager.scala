package com.glyph.scala.lib.entity_property_system
import com.glyph.scala.lib.util.{Pool, Poolable, Indexer}
import com.glyph.libgdx.util.ArrayBag
import com.glyph.scala.Glyph
import java.util

/**
 * @author glyph
 */
class ComponentManager(initialSize:Int){
  val NUMBER_OF_INITIAL_COMPONENT_TYPE = 64
  val components = new ArrayBag[ArrayBag[Component]](NUMBER_OF_INITIAL_COMPONENT_TYPE)
  val componentToIndex = collection.mutable.HashMap.empty[Manifest[_<:Component],Int]
  val componentIndexer = new Indexer(NUMBER_OF_INITIAL_COMPONENT_TYPE)
  val componentPools = new ArrayBag[(Manifest[_<:Poolable],Pool[Poolable])](NUMBER_OF_INITIAL_COMPONENT_TYPE)

  def getComponentMapper[T<:Component](implicit typ:Manifest[T]):ComponentMapper[T]={
    new ComponentMapper[T](components.get(componentIndex[T]).asInstanceOf[ArrayBag[T]])
  }

  def componentPool[T<:Component](implicit typ: Manifest[T]):Pool[T] = {
    val cIndex = componentIndex[T]
    var pool = componentPools.get(cIndex)
    val result = if (pool != null){
      pool
    }else{
      Glyph.log("ComponentManager","new ComponentPool at "+cIndex+" for " + typ.runtimeClass.getSimpleName)
      pool = (typ,new Pool[Poolable].setRuntimeClass(typ.runtimeClass))
      componentPools.set(cIndex,pool)
      pool
    }
    result._2.asInstanceOf[Pool[T]]
  }
  def obtainComponent[T<:Component:Manifest]:T={
    componentPool[T].obtain()
  }
  def freeComponent[T<:Component:Manifest](component:T){
    componentPool[T].free(component)
  }

  def componentIndex[T<:Component](implicit typ: Manifest[T]):Int ={
    val result = componentToIndex get typ match {
      case Some(cIndex)   => cIndex
      case None           =>{
        val index = componentIndexer.getNext()
        componentToIndex(typ) = index
        index
      }
    }
   // Glyph.log("getCIndex",typ.runtimeClass.getSimpleName+":"+result)
    result
  }

  def getComponent[T<:Component](e:Entity)(implicit typ:Manifest[T]):T={
    val cIndex = componentIndex[T]
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

  def addComponent[T<:Component](e:Entity,cIndex:Int,c:T){
    var componentBag = components.get(cIndex)
    if (componentBag == null){
      components.set(cIndex,new ArrayBag[Component](initialSize))
      componentBag = components.get(cIndex)
    }
    componentBag.set(e.index,c)
    e.componentBits.set(cIndex)
  }

  def addComponent[T<:Component](e:Entity,c:T)(implicit typ: Manifest[T]){
    val cIndex = componentIndex[T]
    addComponent(e,cIndex,c)
  }
  def removeComponent[T<:Component](e:Entity,c:T)(implicit typ: Manifest[T]){
    val cIndex = componentIndex[T]
    addComponent(e,cIndex,c)
  }
  def removeComponent[T<:Component](e:Entity,cIndex:Int, c:T)(implicit typ:Manifest[T]){
    components.get(cIndex).set(e.index,null.asInstanceOf[T])
    e.componentBits.clear(cIndex)
  }

  def removeAllComponent(e:Entity){
    var cIndex = 0
    while(cIndex < components.size){
     // Glyph.log("cIndex",""+cIndex)
      val componentBag = components.get(cIndex)
      if (componentBag != null){
        componentBag.set(e.index,null)
      }
      cIndex += 1
    }
    e.componentBits.clear()
  }
  def freeAllComponent(e:Entity){
    var cIndex = 0
    while(cIndex < components.size){
      // Glyph.log("cIndex",""+cIndex)
      val componentBag = components.get(cIndex)
      if (componentBag != null){
        val component = componentBag.get(e.index)
        if (component != null){
          val pool = componentPools.get(cIndex)
          if(pool != null){
            pool._2.free(component)
          }
        }
        componentBag.set(e.index,null)
      }
      cIndex += 1
    }
    e.componentBits.clear()
  }

  def notifyChanged(e:Entity){
    var cIndex = 0
    while(cIndex < components.size){
      val componentBag = components.get(cIndex)
      if (componentBag != null){
        val component = componentBag.get(e.index)
        if (component != null){
          component.onOwnerModified(e)
        }
      }
      cIndex += 1
    }
  }
  def getFilter(interests: Seq[Manifest[_<:Component]]):util.BitSet={
    Glyph.log("create filter")
    val result = new util.BitSet()
    interests.foreach {
      i=>result.set(componentIndex(i))
        Glyph.log("Filter for:",i.runtimeClass.getSimpleName)
    }
    result
  }
}
