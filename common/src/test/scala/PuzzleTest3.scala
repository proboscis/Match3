import com.glyph.scala.game.action_puzzle.{GMatch3, ActionPuzzle}
import com.glyph.scala.lib.util.Logging
import com.glyph.scala.lib.util.reactive.Reactor

/**
 * @author glyph
 */
object PuzzleTest3 extends Reactor with Logging{
  def main(args: Array[String]) {
    val puzzle = new ActionPuzzle
    puzzle.initialize()
    new Thread(new Runnable(){
      def run(){
        var timer = 10f
        val dt = 0.016f
        while(timer > 0){
          timer -= dt
          puzzle.update(dt)
          Thread.sleep(16)
        }
      }
    }).run()
  }
}
