package test

import com.glyph.scala.lib.libgdx.game.ScreenGame
import com.glyph.scala.lib.libgdx.screen.TabledScreen
import com.glyph.scala.ScalaGame
import com.badlogic.gdx.graphics.Texture
import com.glyph.scala.game.puzzle.view.CardToken
import com.glyph.scala.game.puzzle.model.Card
import com.glyph.scala.lib.util.observer.reactive.{Reactor, Var}
import com.glyph.scala.lib.util.observer.Observing
import com.badlogic.gdx.scenes.scene2d.{Touchable, InputEvent, InputListener}


/**
 * @author glyph
 */
object ReactUITest extends ScreenGame(am => {
  var i = 1
  while (i <= 10) {
    am.load("data/card" + i + ".png", classOf[Texture])
    i += 1
  }
}, new TabledScreen with Reactor {
  def STAGE_HEIGHT: Int = ScalaGame.VIRTUAL_HEIGHT

  def STAGE_WIDTH: Int = ScalaGame.VIRTUAL_WIDTH

  def DEBUG: Boolean = true

  val deck = Var((1 to 100) map {i => new Card} toList)
  root.setTouchable(Touchable.enabled)
  root.addListener(new InputListener {
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      println("touch down")
      deck() = deck().tail
      true
    }
  })
  react(deck) {
    d =>
      println(d.size)
      root.clear()
      d foreach {
        card =>
          root.add(new CardToken(card, 100, 100) with Observing {
            observe(press) {
              pos => deck() = deck() ::: deck()
            }
          })
      }
  }
}) with TestGame