import java.io.File
def listFiles(file: File): Seq[String] = file match {
  case f if f.isDirectory => f.listFiles().flatMap(listFiles)
  case f => f.toString :: Nil
}
val files = listFiles(new File("common/src/main/")).filter(_.endsWith(".scala"))
val lines = files.map{
	scala.io.Source.fromFile(_)("UTF-8").getLines.size
}.sum
println(lines)