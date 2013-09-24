package testing

import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.ScalaGame

/**
 * @author glyph
 */
trait DebugScreen extends TabledScreen{
  def STAGE_WIDTH: Int = ScalaGame.VIRTUAL_WIDTH

  def STAGE_HEIGHT: Int = ScalaGame.VIRTUAL_HEIGHT

  def DEBUG: Boolean =  true
}
