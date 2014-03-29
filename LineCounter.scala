import java.io.File
def listFiles(file: File): Seq[String] = file match {
  case f if f.isDirectory => f.listFiles().flatMap(listFiles)
  case f => f.toString :: Nil
}
val lines = listFiles(new File("core/src/main/")).par.filter(_.endsWith(".scala")).map{
  file=>
    val src = scala.io.Source.fromFile(file)("UTF-8")
    try src.getLines().size finally src.close()
}.sum
println(lines)