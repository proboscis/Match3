import java.io.File

implicit def stringIsFile(str:String) = new File(str)
def listFiles(file: File): Seq[File] = file match {
  case f if f.isDirectory => f.listFiles().flatMap(listFiles)
  case f => f :: Nil
}
def convertFilesInFolder(src:File){
  val srcPath = src.getAbsolutePath
  val suffixed = srcPath + "-white"
  val files = listFiles(src).filter(_.getName.endsWith(".png"))
  println(files)
  for(file <- files.par){
    val in = file.getAbsolutePath
    val out = file.getAbsolutePath.replace(srcPath,suffixed)
    new File(out).getParentFile.mkdirs()
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

convertFilesInFolder("C:/Users/glyph/Documents/GitHub/Match3/arts/cc")