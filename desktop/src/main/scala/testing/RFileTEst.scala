package testing

import com.glyph.scala.lib.libgdx.game.ScreenGame
import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.lib.util.reactive.{RFile, Reactor}
import scala.language.dynamics
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.json.{RVJSON, RJSON}

/**
 * @author glyph
 */
object RFileTEst extends ScreenGame(_ => {}, new TabledScreen with Reactor {
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))
  val a = new RFile("json/test.json")

  reactVar(a) {
    println(_)
  }
}) with TestGame
