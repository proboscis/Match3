package com.glyph._scala.lib.libgdx.screen

import com.glyph._scala.lib.util.json.RVJSON
import com.glyph._scala.lib.libgdx.reactive.GdxFile

/**
 * @author glyph
 */
trait ConfiguredScreen extends TabledScreen{
  def configSrc: RVJSON = RVJSON(GdxFile("json/gameConfig.json"))
}
