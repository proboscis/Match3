import java.io.File

val targets = 16::32::48::64::128::256::Nil map(n => n+"x"+n)
val srcDir = "C:/Users/glyph/Desktop/png"
targets.map(s=>new File(srcDir+"/"+s)).par foreach convertFilesInFolder
def listFiles(file: File): Seq[String] = file match {
  case f if f.isDirectory => f.listFiles().flatMap(listFiles)
  case f => f.toString :: Nil
}
def convertFilesInFolder(folder:File){
  val files = listFiles(folder).view.filter(_.endsWith(".png")).map(new File(_)).map(_.getName).force
  val suffix = "-white"
  new File(folder.getAbsolutePath+suffix).mkdirs()
  for(file <- files.par){
    val in = folder.getAbsolutePath+"\\"+file
    val out = folder.getAbsolutePath+suffix+"\\"+file
    println("converting ",file)
    println(in)
    println(out)
    //shells dont work well on windows... damnit!
    //Process(s"convert -negate ${srcDir+"/"+file} ${tgtDir+"/"+file}").run()
    //Runtime.getRuntime.exec(Array("convert","-negate",in,out))
    Runtime.getRuntime.exec(s"cmd.exe /c convert -negate $in $out")//this works! finally!
    //println("finished converting "+file)
  }
}