import org.scalacheck.Properties
import org.scalacheck.Prop._
import scalaz._
import Scalaz._
/**
 * @author glyph
 */
object JacksonTest extends Properties("jackson") {
  //TODO use Try!
  property("serialization") = forAll{
    (i:Int)=>{
      import com.codahale.jerkson.Json._

      // Parse JSON arrays
      parse[List[Int]]("[1,2,3]") //=> List(1,2,3)

      // Parse JSON objects
      parse[Map[String, Int]]("""{"one":1,"two":2}""") //=> Map("one"->1,"two"->2)

      // Parse JSON objects as case classes
      // (Parsing case classes isn't supported in the REPL.)
      case class Person(id: Long, name: String)
      val p = parse[Person]("""{"id":1,"name":"Coda"}""") //=> Person(1,"Coda")
      println(p)
      /*
      // Parse streaming arrays of things
      for (person <- stream[Person](System.in)) {
        println("New person: " + person)
      }
      */
      true
    }
  }
}
