package com.glyph.scala.lib.util.tile

import model.{Layer, TileMap}
import com.glyph.scala.lib.util.json.JSON._

/**
 * @author glyph
 */
class JsonMapParser {
  def parse(jsonStr: String): TileMap = {
    val json = parseJSON(jsonStr)
    val map = new TileMap(
      json.width,
      json.height,
      json.tilewidth,
      json.tileheight,
      json.layers.map {
        j => new Layer(j.width, j.height, j.data.map {
          _.toInt
        })
      })
    println(map)
    map
  }
}
