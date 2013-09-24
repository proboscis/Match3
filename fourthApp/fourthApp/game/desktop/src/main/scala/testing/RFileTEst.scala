package testing

import com.glyph.scala.lib.libgdx.game.ScreenGame
import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.lib.util.reactive.{RFile, Reactor}
import com.glyph.scala.lib.util.json.DepreactedRJSON
import scala.language.dynamics
/**
 * @author glyph
 */
object RFileTEst extends ScreenGame(_ => {}, new TabledScreen with Reactor{
  def STAGE_HEIGHT: Int = 100

  def STAGE_WIDTH: Int = 100

  def DEBUG: Boolean = true
  val a = new RFile("json/test.json")

  reactVar(a){
    println(_)
  }
}) with TestGame
