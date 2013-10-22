import com.glyph.scala.game.action_puzzle.Animation
import org.scalacheck.Properties
import org.scalacheck.Prop._

/**
 * @author glyph
 */
object AnimationTest extends Properties("Animation") {

  import Animation._

  property("concat") = forAll {
    (a: String, b: String, c: String) => {

      val anim: String ~> String = str => cb => cb(str)
      val anim2: String ~> String = str => cb => cb(str + b)
      val connected = anim ~> anim2 ~> anim2
      var result: String = ""
      connected(a)(str => result = str)
      println(result)
      result == a + b + b
    }
  }

}
