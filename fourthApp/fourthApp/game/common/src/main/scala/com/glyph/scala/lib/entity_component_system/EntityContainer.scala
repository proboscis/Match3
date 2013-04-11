package com.glyph.scala.lib.entity_component_system

import collection.mutable.{ListBuffer, HashMap}
import com.badlogic.gdx.Gdx
import collection.mutable
import java.lang.reflect.{Field, Method}
import com.glyph.scala.Glyph

/**
 * Created with IntelliJ IDEA.
 * User: glyph
 * Date: 13/04/02
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
class EntityContainer {
  private val DEBUG = false
  private val TAG = "EntityContainer"
  private val adapterToFilterMap = HashMap.empty[Manifest[_], Filter]
  private val adapterListMap = HashMap.empty[Manifest[_], mutable.ListBuffer[Adapter]]
  private val entities = ListBuffer.empty[Entity]
  private val mSystems = ListBuffer.empty[GameSystem]

  /**
   * add an entity
   * @param e
   */
  def addEntity(e: Entity) = {
    entities += e
    for ((adapterManifest, filter) <- adapterToFilterMap) {
      if (DEBUG) Gdx.app.log(TAG, "acceptCheck")
      if (e.contains(filter.receptors.map(t => t._2))) {
        if (DEBUG) Gdx.app.log(TAG, "accept->" + adapterManifest.runtimeClass.getSimpleName)
        val list: mutable.ListBuffer[Adapter] = adapterListMap get adapterManifest match {
          case Some(x) => x
          case None => {
            val newList = ListBuffer.empty[Adapter]
            adapterListMap(adapterManifest) = newList
            newList
          }
        }
        val adapter = adapterManifest.runtimeClass.getConstructor(classOf[Entity]).newInstance(e).asInstanceOf[Adapter]
        if (DEBUG) Gdx.app.log(TAG, "new Adapter->" + adapter.getClass.getSimpleName)
        for (receptor <- filter.receptors) {
          if (DEBUG) Gdx.app.log(TAG, "\t" + receptor._1.getName)
          receptor._1.setAccessible(true)
          receptor._1.set(adapter, e.get(receptor._2))
        }
        list += adapter
      }
    }
    mSystems.foreach(_.onAddEntity(this,e))
  }
  def removeEntity(e:Entity){
    entities -= e
    for((adapterManifest,adapterList) <- adapterListMap){
      for (adapter <- adapterList){
        if (adapter.consistsOf(e)){
          adapterList -= adapter
        }
      }
    }
  }

  /**
   * add an adapter
   * @param typ
   * @tparam T
   */
  def addAdapter[T](implicit typ: Manifest[T]) = adapterToFilterMap get typ match {
    case Some(x) =>
    case None => adapterToFilterMap(typ) = new Filter(typ)
  }

  /**
   * add GameSystem
   * @param s
   */
  def addSystem(s:GameSystem){ mSystems += s}

  /**
   * remove GameSystem
   * @param s
   */
  def removeSystem(s:GameSystem){ mSystems -= s}
  /**
   * retrieve adapters from entities. this may return Unit
   * @param typ
   * @tparam T
   * @return
   */
  def getAdapters[T](implicit typ: Manifest[T]):Seq[T] = adapterListMap get typ match {
    case Some(x) => x.asInstanceOf[Seq[T]]
    case None => null
  }

  def update(){
    mSystems.foreach(_.update(this))
  }

  /**
   * filter for filtering entity
   * @param typ
   */
  class Filter(val typ: Manifest[_]) {
    if (DEBUG) Gdx.app.log(TAG, "new filter for" + typ.getClass.getSimpleName)
    val receptors = ListBuffer.empty[(Field, Manifest[_])]
    for (field <- typ.runtimeClass.getDeclaredFields
         if field.isAnnotationPresent(classOf[Receptor])) {
      if (DEBUG) Gdx.app.log(TAG, "\tfound receptor! ->" + field.getName)
      receptors += ((field, Manifest.classType(field.getType)))
    }
  }
}
