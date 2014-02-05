import com.glyph._scala.lib.util.reactive.{Varying, Var}
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll
import scalaz._
import Scalaz._
import scalaz.Alpha.V

/**
 * @author glyph
 */
object FunctorTest extends Properties("Functor2") {
  property("injection") = forAll {
    (a: Int, b: Int) => {
      val mm: Option[Varying[Int]] = some(Var(a))
      val mapped = mm.mapp {
        _ + b
      }
      mapped.get.apply() == some(Var(a + b)).get.apply()//this actually compiles..
    }
  }

  trait TestOps[T, B[_], A[B]] {
    def mapp[R](f: T => R): A[B[R]]
  }

  implicit def toTestOps[T, B[_] : Functor, A[B] : Functor](nested: A[B[T]]): TestOps[T, B, A] = new TestOps[T, B, A] {
    def mapp[R](f: (T) => R): A[B[R]] = nested.map(_.map(f))
  }

}
