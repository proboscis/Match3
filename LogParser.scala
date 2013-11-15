import util.parsing.combinator.JavaTokenParsers

val Tokenize = """.*(\(.*\)).*""".r
val Start = """(\(.*,.*\))""".r
val End = """(\(\d+\))""".r
val tokens = io.Source.stdin.getLines.collect {
	case Tokenize(token) =>token
}.toList.collect {
	case Start(s) => s
	case End(s) => s
}.take(1000)
case class Call(key:String,time:Long)
case class Node(call:Call,children:List[Node]){
	override def toString = (call.key +"->"+call.time+":"+children.mkString(","))
}
object Parser extends JavaTokenParsers {
  //symbols are defined by alphabets
  def symbol: Parser[String] = """[a-zA-Z]+\d*""".r
  def number: Parser[Long] = """\d+""".r ^^ {_.toLong/1000}
  def start:Parser[String] = "("~symbol~","~symbol~")" ^^{case "("~a~cm~b~")" =>a+":"+b}
  def end:Parser[Long]= "("~number~")"^^{case "("~n~")" => n}
  //def tree:Parser[Node] = start ~ node ~ end ^^{case s~n~e => Node(Call(s,e),n)}
  def node:Parser[Node] = start ~ rep(node) ~ end ^^{case s~l~e => Node(Call(s,e),l)}
  def token:Parser[Any] = node | start | end
/*
  //abstraction is surrounded by parenthesis and starts from '@'
  def abs: Parser[Abs] = "("~>"@"~>symbol~token<~")"^^{ case s~t => Abs(s,t)}
  //application is surrounded by parenthesis and consists of two tokens
  def app: Parser[App] = "("~>token~token<~")"^^{case a~b=> App(a,b)}
  //unit is a token surrounded by parenthesis.
  def unit:Parser[Any] = "("~>token<~")"
  //tokens are consisting of application, abstraction, or symbols.
  def token:Parser[Any] = app | abs | symbol|unit
  */
  //parses the given string and returns list of application
  def parse(str: String) = parseAll(rep(token), str)
}

object NodePrinter{
	def format(node:Node):List[String] = {
		val map = depthMap(node)
		def tail(node:Node,depth:Int):List[String] ={
			val str = ("%"+map(depth)+"s-%s").format("",node.call+"")
			node match{
				case Node(call,Nil) =>str::Nil
				case n@Node(call,children) => children.foldLeft(str::Nil){
					case (list,child) => list:::tail(child,depth+1)
				} 
			}
		}
		tail(node,0)
	}
	def getWidth(node:Node):Int = node match{
		case Node(call,Nil) => call.key.size
		case Node(call,children) => children.map(getWidth).sum
	}
	def depthMap=(node:Node) => {
		def tail(node:Node,depth:Int,position:Int):Int Map Int ={
			val map = Map(position -> (depth+(node.call.key.size)))
			node match{
				case Node(call,Nil) => map
				case Node(call,children) => children.foldLeft(map){
					case (map,child) => map++tail(child,depth+call.key.size,position+1)
				} 
			}
		}
		tail(node,0,0)
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
val table = Tabulator.format(("name"::"count"::"samples"::Nil)+:map)
println(table)
Parser.parse(tokens.mkString("")) match{
	case Parser.Success(result,next) => {
		result foreach{
			case n@Node(_,_) => println(n)
			case any => println(any)
		}
	}
	case any => println(any)
}