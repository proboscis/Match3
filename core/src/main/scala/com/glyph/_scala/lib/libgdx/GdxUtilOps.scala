package com.glyph._scala.lib.libgdx

import com.glyph._scala.lib.libgdx.GdxUtilOps.SnapshotArrayUtil
import com.badlogic.gdx.utils.SnapshotArray

/**
 * @author glyph
 */
trait GdxUtilOps {
  implicit def saToSAUtil[T](sa:SnapshotArray[T]):SnapshotArrayUtil[T] = new SnapshotArrayUtil[T](sa)
}
object GdxUtilOps extends GdxUtilOps{
  implicit class SnapshotArrayUtil[T](target:com.badlogic.gdx.utils.SnapshotArray[T]){
    def traverse(f:T=>Unit):Unit = {
      val array = target.begin()
      val l = target.size
      var i = 0
      while (i < l){
        f(array(i))
        i += 1
      }
      target.end()
    }
  }
}
