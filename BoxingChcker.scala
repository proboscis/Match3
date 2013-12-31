import scala.sys.process._
import java.io.{PrintWriter, File}

val classPath = "./common/target/scala-2.10/classes"
val root = new File(classPath)
def listFiles(file: File): Seq[String] = file match {
  case f if f.isDirectory => f.listFiles().flatMap(listFiles)
  case f => f.toString :: Nil
}
val resultFile = new File("./boxing.txt")
val writer = new PrintWriter(resultFile)
val files = listFiles(root).filter(_.endsWith(".class"))
println("processing %d class files..." format files.length)
try {
  files foreach {
    file =>
      val stream = "javap -c -classpath %s %s".format(classPath, file).lines_!.filter(_.contains("boxToFloat"))
      if(stream.size != 0){
        writer.println(file)
        stream foreach writer.println
      }
  }
} finally {
  writer.close()
}
//"javap -c -classpath %s com/glyph/scala/lib/util/reactive/Varying$mcI$sp.class".format(classPath).lines_!.foreach(println)
