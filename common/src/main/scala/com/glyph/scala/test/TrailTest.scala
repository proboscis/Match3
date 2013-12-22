package com.glyph.scala.test

import com.glyph.scala.lib.libgdx.screen.{ConfiguredScreen, ScreenBuilder}
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.Screen
import scala.collection.mutable.ArrayBuffer

/**
 * @author glyph
 */
class TrailTest extends ConfiguredScreen{

  class Trail{
    val records = ArrayBuffer[Float]()
    val vertices = new Array[Float](2*2*1000)
    val texCoordsV = new Array[Float](2*1000)
  }
}


