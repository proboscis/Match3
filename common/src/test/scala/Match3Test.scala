import com.badlogic.gdx.math.MathUtils
import com.glyph.scala.game.action_puzzle.GMatch3
import com.glyph.scala.lib.util.reactive.{Reactor, Var}
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import scala.collection.mutable.ArrayBuffer

/**
 * @author glyph
 */

object Match3Test extends Properties("Match3"){
  class P(val n:Int) extends GMatch3.Panel{
    def matchTo(panel: GMatch3.Panel): Boolean = panel match{
      case p:P => n == p.n
      case any => false
    }
    override def toString:String = n+""

  }
  property("match3") = forAll{
    (a:Int) => {

      import GMatch3._
      val ROW = 6
      val COLUMN = 6
      val reactor = new Reactor{}
      val puzzle:Var[Puzzle[P]] = Var(GMatch3.initialize[P](ROW))
      reactor.reactVar(puzzle)(p => println(p.text))
      val seed = () => new P(MathUtils.random(0,5))
      puzzle() = puzzle().createFillingPuzzle(seed,COLUMN)

      println("===================")
      println(puzzle().text)
      println("previous")
      val prev = puzzle().scanAllWithException(3)(_=>false).map(_.map(_._1))
      println(prev)
      println("current")
      //GMatch3.allLine(puzzle())(ROW)(COLUMN) foreach println
      val current = GMatch3.scanAll(puzzle())(ROW)(COLUMN){
        (a,b) => if(a != null && b != null) a.n == b.n else false
       }.filter(_.size >= 3)
      /*
      val seq = List(1,1,1,2,2,2,1,3,1,2,1,2,1,0,0,0)
      GMatch3.segment2(seq)(_==_) foreach println
      */

      println(current)
      prev.size == current.size
    }
  }
}
