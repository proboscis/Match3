package testing

import com.glyph.scala.lib.libgdx.game.ScreenGame
import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.puzzle.view.CardToken
import com.glyph.scala.lib.util.observer.Observing
import com.badlogic.gdx.scenes.scene2d.{Touchable, InputEvent, InputListener}
import com.glyph.scala.game.puzzle.model.cards.Charge
import com.glyph.scala.lib.util.reactive.{Var, Reactor}
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.json.{RVJSON, RJSON}


/**
 * re-creating view hierarchy is not reasonable...
 * @author glyph
 */
object ReactUITest extends ScreenGame(am => {
  var i = 1
  while (i <= 10) {
    am.load("data/card" + i + ".png", classOf[Texture])
    i += 1
  }
}, new TabledScreen with Reactor {
  def configSrc = RVJSON(GdxFile("json/gameConfig.json"))

  val deck = Var((1 to 2) map {
    i => new Charge
  } toList)
  root.setTouchable(Touchable.enabled)
  root.addListener(new InputListener {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      println("touch down")
      deck() = deck().tail
      true
    }
  })
}) with TestGame