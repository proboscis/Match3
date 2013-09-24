package testing

import com.glyph.scala.lib.libgdx.game.Resettable


/**
 * @author glyph
 */
trait UITest extends TestGame with Resettable{
  def screen: DebugScreen
  def create() {
    setScreenConstructor(screen)
  }
}
