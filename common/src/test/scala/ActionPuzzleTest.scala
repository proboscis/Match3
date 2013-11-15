import com.glyph.scala.game.action_puzzle.{GMatch3, ActionPuzzle}
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.util.reactive.Reactor
import org.scalacheck.{Prop, Properties}
import Prop._

/**
 * @author glyph
 */
object ActionPuzzleTest extends Logging{
  def main(args: Array[String]) {
    val puzzle = new ActionPuzzle
    val reactor = new Reactor {
      import GMatch3._
      reactVar(puzzle.statics) {
        s => log("statics:" + s.text)
      }
      reactVar(puzzle.floatings) {
        s => //log("floating:" + s.text)
      }
      reactVar(puzzle.future) {
        s => //log("future:" + s.text)
      }
    }
    puzzle.initialize() {
      result => println(result)
    }
    new Thread(new Runnable {
      def run(){
        val dt = 0.016f
        var timer = 1000f
        while (timer > 0) {
          puzzle.update(dt)
          timer -= dt
          Thread.sleep(16)
        }
        reactor.clearReaction()
      }
    }).start()

  }
}
