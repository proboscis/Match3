package testing

import com.glyph.scala.lib.libgdx.actor.ui.RLabel
import com.glyph.scala.game.puzzle.view._
import com.badlogic.gdx.graphics.Color
import com.glyph.scala.lib.util.json.RJSON
import com.glyph.scala.lib.util.reactive.{Reactor, RFile}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.glyph.scala.lib.libgdx.actor.action.MyActions


/**
 * @author glyph
 */
object BounceTest extends UITest {
  def screen: DebugScreen = new DebugScreen with Reactor {
    val json = RJSON(new RFile("common/src/main/resources/test/test.js").getString)
    val label = new RLabel(skin, json.i.as[String].map {
      _.getOrElse("None")
    })
    label.setColor(Color.BLACK)
    root.add(label)
    root.layout()
    reactVar(json) {
      j => import Actions._
      //Interpolation.
        for {
          interpolation <- j.interpolation.as[Interpolation]
          yAmount <- j.yAmount.as[Int]
          } {
          val m = moveBy(0, yAmount, 1, interpolation)
          label.addAction(MyActions.jump(100,1))
        }
    }
  }
}

