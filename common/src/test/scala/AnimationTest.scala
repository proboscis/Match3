import com.glyph.scala.game.action_puzzle.ActionPuzzle
import org.scalacheck.Properties
import org.scalacheck.Prop._

/**
 * @author glyph
 */
object AnimationTest extends Properties("Animation"){
  import ActionPuzzle._
  property("concat") = forAll {
    (a:String,b:String,c:String)=>{
      val animation:Animation[String,String] = str => cb => cb(str)
      val anim2:Animation[String,String] = str => cb => cb (str + b)
      val connected = concat2(animation,anim2)
      var result:String = ""
      connected(a)(str => result = str)
      println(result)
      result == a + b
    }
  }
}
