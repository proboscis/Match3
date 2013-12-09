package com.glyph.scala.lib.libgdx.screen

import com.glyph.scala.lib.util.json.RVJSON
import com.glyph.scala.lib.libgdx.reactive.GdxFile

/**
 * @author glyph
 */
trait ConfiguredScreen extends TabledScreen{
  def configSrc: RVJSON = RVJSON(GdxFile("json/gameConfig.json"))
}
