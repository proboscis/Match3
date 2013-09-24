import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.puzzle.controller.PuzzleGameController
import com.glyph.scala.game.puzzle.model.cards.{Card, Scanner, Meteor}
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import com.glyph.scala.lib.util.reactive.RScala
val c1 = new RScala[PuzzleGameController => Unit](new GdxFile("scala/card/invMeteor.scala"))
val square = new RScala[PuzzleGameController => Unit](new GdxFile("scala/card/square.scala"))
() => {
  MathUtils.random(0,3) match {
    case 0 => new Meteor
    case 1 => new Scanner
    case 2 => new Card {
      def apply(controller: PuzzleGameController) {
        c1()(controller)
      }
    }
    case 3 => new Card{
      def apply(controller: PuzzleGameController) {
        square()(controller)
      }
    }
  }
}