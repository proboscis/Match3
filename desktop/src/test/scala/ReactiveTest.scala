import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import scalaz._
import Scalaz._
import com.glyph._scala.lib.util.reactive._

/**
 * @author glyph
 */
object ReactiveTest extends Properties("Var") {
  property("semigroup") = forAll {
    (a: Int, b: Int) => {
      val a = Var(1)
      val b = Var(3)
      val c = a.* |+| b.*
      a() = 2
      b() = 3
      println(a, b, c)
      (a() + b()) == c()
    }
  }
  property("apllicative builder") = forAll {
    (a1: Int, b1: String, c1: Double) => {
      // Some(1) |+| Some(2)
      val a = Var(a1)
      val b = Var(b1)
      val c = Var(c1)
      val d = (a.* |@| b.* |@| c.*) {
        _ + _ + _
      }
      a() *= 2
      b() += "bbb"
      c() *= 0.12
      println(a, b, c, d)
      (a() + b() + c()) == d()
    }
  }
}