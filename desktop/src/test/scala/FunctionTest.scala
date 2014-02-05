import com.glyph._scala.lib.util.collection.list.DoubleLinkedList
import scala.collection.mutable.ArrayBuffer

/**
 * @author glyph
 */
object FunctionTest {
  def bench(tag:String)(block: =>Unit):Seq[Any] = {
    println("start:"+tag)
    val start = System.nanoTime()
    block
    val end = System.nanoTime()
    val result = tag::(end-start)/1000::Nil
    println(result)
    result
  }
  def method(){val i = 0}

  def main(args: Array[String]) {
    val list:List[Int] = (0 to 10).toList
    val array = Array(list:_*)
    val dlist = new DoubleLinkedList[Int]
    list foreach dlist.push
    val L = 10000000//Int.MaxValue
    val benches = bench("ops check +="){
      var i = 0
      while(i < L){
        i += 1
      }
    }::
    bench("ops check = +1"){
      var i = 0
      while(i < L){
        i  = i + 1
      }
    }::
        bench("new func"){
          var i = 0
          while( i < L){
            val f = (_:Int)+1
            i += 1
          }
        }::
        bench("method call"){
          var i = 0
          while( i < L){
            method()
            i += 1
          }
        }::
    bench("cached"){
      val fun = (any:Any) => {}
      var i = 0
      while( i < L){
        list foreach fun
        i += 1
      }
    }::
    bench("tail"){
      val fun = (any:Any) => {}
      var i = 0
      while( i < L){
        var l = list
        while(!l.isEmpty){
          l = l.tail
        }
        i += 1
      }
    }::
    bench("list anonnfun foreach"){
      var i = 0
      while( i < L){
        list foreach{
          _=>
        }
        i+=1
      }
    }::bench("array foreach"){
        var i = 0
        while( i < L){
          array foreach{
            _=>
          }
          i+=1
        }
      }::bench("array index"){
        var i = 0
        while( i < L){
          var x = 0
          val len = array.length
          while(x < len){
            array(x)
            x += 1
          }
          i+=1
        }
      }::bench("dlist foreach"){
        var i = 0
        while( i < L){
          dlist foreach{
            _=>
          }
          i+=1
        }
      }::Nil
    println(Tabulator.format(("tag"::"time"::Nil)+:benches))
  }
}
object Tabulator {
  def format(table: Seq[Seq[Any]]) = table match {
    case Seq() => ""
    case _ =>
      val sizes = for (row <- table) yield (for (cell <- row) yield if (cell == null) 0 else cell.toString.length)
      val colSizes = for (col <- sizes.transpose) yield col.max
      val rows = for (row <- table) yield formatRow(row, colSizes)
      formatRows(rowSeparator(colSizes), rows)
  }

  def formatRows(rowSeparator: String, rows: Seq[String]): String = (
    rowSeparator ::
      rows.head ::
      rowSeparator ::
      rows.tail.toList :::
      rowSeparator ::
      List()).mkString("\n")

  def formatRow(row: Seq[Any], colSizes: Seq[Int]) = {
    val cells = (for ((item, size) <- row.zip(colSizes)) yield if (size == 0) "" else ("%-" + size + "s").format(item))
    cells.mkString("|", "|", "|")
  }

  def rowSeparator(colSizes: Seq[Int]) = colSizes map { "-" * _ } mkString("+", "+", "+")
}
