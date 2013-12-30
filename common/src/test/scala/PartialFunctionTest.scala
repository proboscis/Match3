import scala.None

/**
 * @author glyph
 */
object PartialFunctionTest{
  val a :Option[Int] = Some(100)
  val b = A("aaa")

  val ff = ()=>a match{
    case Some(0)=>
    case None=>
    case _=>
  }
  val f = ()=> b match{
    case A("aa")=>
    case A(s)=>
    case _=>
  }
  val tree = ()=>{
    try{
      println("trying")
    } catch {
      case e:Throwable => e.printStackTrace()
    }
  }
}
case class A(test:String)
/*
object A{
  def unapply(a:String):Option[Int] = Some(a.toInt)
}
*/