package testing

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.ScalaGame
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.json.RJSON

/**
 * @author glyph
 */
trait DebugScreen extends TabledScreen{
  def configSrc = RJSON(GdxFile("json/gameConfig.json").getString)
}
