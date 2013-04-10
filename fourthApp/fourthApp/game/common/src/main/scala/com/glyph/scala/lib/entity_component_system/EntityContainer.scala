package com.glyph.scala.lib.entity_component_system

import collection.mutable.{ListBuffer, HashMap}
import com.badlogic.gdx.Gdx
import collection.mutable
import java.lang.reflect.{Field, Method}

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
  private val AdapterListMap = HashMap.empty[Manifest[_], mutable.ListBuffer[Any]]
  private val entities = ListBuffer.empty[Entity]

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
        val list: mutable.ListBuffer[Any] = AdapterListMap get adapterManifest match {
          case Some(x) => x
          case None => {
            val newList = ListBuffer.empty[Any]
            AdapterListMap(adapterManifest) = newList
            newList
          }
        }
        val adapter = adapterManifest.runtimeClass.newInstance()
        if (DEBUG) Gdx.app.log(TAG, "new Adapter->" + adapter.getClass.getSimpleName)
        for (receptor <- filter.receptors) {
          if (DEBUG) Gdx.app.log(TAG, "\t" + receptor._1.getName)
          receptor._1.setAccessible(true)
          receptor._1.set(adapter, e.get(receptor._2))
        }
        list += adapter
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
   * retrieve adapters from entities. this may return Unit
   * @param typ
   * @tparam T
   * @return
   */
  def getAdapters[T](implicit typ: Manifest[T]):Seq[T] = AdapterListMap get typ match {
    case Some(x) => x.asInstanceOf[Seq[T]]
    case None => null
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
