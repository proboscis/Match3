import org.scalacheck.Properties
import org.scalacheck.Prop._

/**
 * @author glyph
 */
object FloatTest extends Properties("FloatTest") {
  property("floating?") = forAll {
    (a: Int) => {
      true
    }
  }
}
