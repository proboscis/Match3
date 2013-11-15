val Matches = """.*\((.*)\)(\d+).*""".r
val Key = """(.*),(.*)""".r
val map = io.Source.stdin.getLines.collect {
	case Matches(Key(clazz,method),ellapsed) => ((clazz,method),ellapsed.toLong/1000/1000)
}.toList.groupBy(_._1).map{
	case (info,list)=>{
		val times = list.map(_._2).sorted.reverse
		(info,("count:"+times.size,times.take(30),times.sum.toFloat/times.size.toFloat))
	}
}.toList.sortBy{
	case (info,(count,samples,mean)) => samples.head
}.reverse.map{
	case ((clz,method),(count,samples,mean)) => clz::method::count::"%03.1f".format(mean)::samples.map("%3s".format(_)).mkString(",")::Nil
}
val table = Tabulator.format(("class"::"method"::"count"::"mean"::"samples"::Nil)+:map)
println(table)


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