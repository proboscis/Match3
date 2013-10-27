import com.glyph.scala.game.action_puzzle.ActionPuzzle._
import com.glyph.scala.lib.puzzle.Match3.{Panel, Puzzle}
import org.scalacheck.Properties
import org.scalacheck.Prop._
import scalaz._
import Scalaz._

/**
 * @author glyph
 */
object FloatTest extends Properties("FloatTest") {
  property("floating?") = forAll {
    (a: Int) => {
      val puzzle: Puzzle = for (i <- 0 to 5) yield for (j <- 0 to 5) yield (i % 3) * (j % 3): Panel
      "puzzle".println
      puzzle.text |> println
      val scanned = puzzle.scanAll.flatten.map {
        case (p, x, y) => p
      }.distinct
      println(scanned)
      val(left,floating) = puzzle.remove(scanned)
      "left".println
      left.text |> println
      "floating".println
      floating.text |> println
      true
    }
  }
}
