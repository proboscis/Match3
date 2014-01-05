package com.glyph.scala.test

import java.io.{PrintWriter, File}
import com.glyph.scala.lib.util.reactive.{Reactor, Var, Varying}
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import com.googlecode.scalascriptengine.{SourcePath, Config, ScalaScriptEngine}
import com.glyph.scala.lib.util.Logging
import scala.reflect.io.{Path, Directory}

/**
 * @author glyph
 */
object ScriptEngineTest extends Reactor with Logging {
  def main(args: Array[String]) {
    /*
    val srcChecker = new SourceChecker("./common/src/main/scala", "./.changed")
    srcChecker.register[RFile]
    srcChecker.start()
    */
    val classChecker = new ClassScripter("./common/src/main/scala", "./.changed", "./.changed/classes")
    log("start classChecker")
    classChecker.start()
    log("getClass")
    val cls = classChecker.getClass[Test, Hello]
    reactSome(cls) {
      c => {
        log("class changed", c)
        println(c.newInstance().result)
      }
    }
    /*
    val sourcePath = "./scripts"
    val classPath = "./common/target/scala-2.10/class"
    val classDir = new File(classPath)
    val sourceDir = new File(sourcePath)
    println(classDir.getAbsolutePath)
    val sse = ScalaScriptEngine.onChangeRefresh(sourceDir)
    //val sse = new ScalaScriptEngine(Config(SourcePath(sourceDir,classDir)::Nil))
    sse.deleteAllClassesInOutputDirectory()
    println("refreshing")
    sse.refresh
    println("trying to create a new instance")
    while (true) {
      Thread.sleep(500)
      println(sse.get[Test]("TestAB").newInstance().result)
    }
    */

    /*
    // the source directory
    println(ScalaScriptEngine.currentClassPath.map(_.getAbsolutePath))
    val sourceDir = new File("./common/src/main/resources")
    // compilation classpath
    val compilationClassPath = ScalaScriptEngine.currentClassPath
    // runtime classpath (empty). All other classes are loaded by the parent classloader
    val runtimeClasspath = Set[File]()
    // the output dir for compiled classes

    val outputDir = new File(System.getProperty("java.io.tmpdir"), "scala-script-engine-classes")
    outputDir.mkdir

    //val outputDir = new File("./common/target/scala-2.10/classes")
    val sse = new ScalaScriptEngine(Config(
      List(SourcePath(sourceDir, outputDir)),
      compilationClassPath,
      runtimeClasspath
    )) with RefreshAsynchronously with FromClasspathFirst {
      val recheckEveryMillis: Long = 1000 // each file will only be checked maximum once per second
    }

    // delete all compiled classes (i.e. from previous runs)
    sse.deleteAllClassesInOutputDirectory()
    // since the refresh occurs async, we need to do the 1st refresh otherwise initially my.TryMe
    // class will not be found
    sse.refresh

    while (true) {
      val t = sse.newInstance[Test]("TestAB")
      println(sse.currentVersion.classLoader)
      println("code version %d, result : %s".format(sse.versionNumber, t.result))
      Thread.sleep(500)
    }
    */

  }
}

trait FileChecker extends Logging {
  val interval = 1000
  var registeredFiles: File Map (Var[File], Long) = Map()

  def onFileChange(f: File)

  def start() {
    new Thread(new Runnable {
      def run() {
        while (true) {
          //log("check")
          // log(registeredFiles)
          Thread.sleep(interval)
          registeredFiles.synchronized {
            registeredFiles ++= registeredFiles collect {
              case (file, (cb, time)) if file.lastModified() != time => {
                log("file is changed!", file.getAbsolutePath)
                onFileChange(file)
                cb() = file
                file ->(cb, file.lastModified())
              }
            }
          }
        }
      }
    }).start()
  }

  def getFile(fileName: String): Varying[File] = {
    log("getFile", fileName)
    val file = new File(fileName)
    registeredFiles.get(file) match {
      case Some(s) => s._1
      case None =>
        log(file)
        val pair = Var(file) -> file.lastModified()
        registeredFiles.synchronized {
          registeredFiles += file -> pair
        }
        pair._1
    }
    registeredFiles(new File(fileName))._1
  }
}

class SourceChecker(sourceDir: String, outputDir: String)
  extends FileChecker
  with Reactor
  with com.glyph.scala.lib.util.Logging {
  val prefix = "changed"
  val sourceDirFile = new File(sourceDir)
  val outputDirFile = new File(outputDir)
  var versionMap: File Map Int = Map() withDefault (_ => 0)

  def tagToDir(c: ClassTag[_]) = c.runtimeClass.getCanonicalName.replace(".", "/") + ".scala"

  def get[T: ClassTag]: Varying[File] = getFile(sourceDir + "/" + tagToDir(implicitly[ClassTag[T]]))

  def onFileChange(file: File): Unit = {
    copyToOutput(file)
  }

  protected def copyToOutput(src: File) {
    val srcParentFile = src.getParentFile.getAbsolutePath.replace(sourceDirFile.getAbsolutePath, "")
    val srcFileDir = src.getAbsolutePath
    val name = src.getName
    log("srcName", srcParentFile, name)
    val changeDir = outputDir + "/" + srcParentFile + "/" + prefix + versionMap(src) + name
    val changeFile = new File(changeDir)
    changeFile.getParentFile.mkdirs()
    val out = new PrintWriter(changeDir)
    var replaced = false
    val in = scala.io.Source.fromFile(srcFileDir)
    val lines = in.getLines()
    while (lines.hasNext) {
      val line = lines.next()
      if (!replaced) {
        val regex = """(class|trait|object)\s+(([a-zA-Z\d_$])*)""".r
        out.println(regex.findFirstMatchIn(line) match {
          case Some(m) => {
            replaced = true
            log(line)
            log(m.groupCount)
            for (i <- 0 until m.groupCount) {
              log(i, m.group(i) + "")
            }
            val str = m.group(2)
            if (str != null) line.replace(str, prefix + versionMap(src) + str) else line
          }
          case None => line
        })
      } else {
        out.println(line)
      }
    }
    versionMap += src -> versionMap(src)
    in.close()
    out.close()
    /*
    val inTry = Try(new FileInputStream(srcFileDir).getChannel)
    val outTry = Try(new FileOutputStream(changeDir).getChannel)
    val transTry = (for (in <- inTry; out <- outTry) yield {
      Try {
        in.transferTo(0, in.size(), out)
      }
    }).flatten
    inTry :: outTry :: transTry :: Nil foreach {
      case Success(s: FileInputStream) => s.close()
      case Success(_) =>
      case Failure(f) => f.printStackTrace()
    }
    */
  }
}

class ClassScripter(srcDir: String, outDir: String, classDir: String) extends SourceChecker(srcDir, outDir) with Logging {
  val sourcePath = outDir
  val sourceDir = new File(sourcePath)
  new File(classDir).mkdirs()
  Directory.apply(Path(sourcePath)).deleteRecursively()
  val sse = new ScalaScriptEngine(Config(SourcePath(sourceDir, new File(classDir)) :: Nil))
  sse.deleteAllClassesInOutputDirectory()
  log("first refresh")
  try {
    sse.refresh
  } catch {
    case e: Throwable => e.printStackTrace()
  }
  log("done refreshing")

  override def onFileChange(file: File): Unit = {
    super.onFileChange(file)
    log("fileChange")
    try {
      sse.refresh
    } catch {
      case e: Throwable => e.printStackTrace()
    }
  }

  def try2Opt[T](t: Try[T]): Option[T] = t match {
    case Success(s) => Some(s)
    case Failure(f) => f.printStackTrace(); None
  }

  def getClass[I, T <: I : ClassTag]: Varying[Option[Class[I]]] = {
    log("getClass")
    super.get[T].map {
      file => {
        log(("mapFileToClass", file.getAbsolutePath))
        val tag = implicitly[ClassTag[T]]
        val canon = tag.runtimeClass.getCanonicalName
        val simple = tag.runtimeClass.getSimpleName
        val replaced = canon.replace(simple, prefix + versionMap(file) + simple)
        log("replaced", replaced)
        val opt = try2Opt(Try(sse.get[I](replaced)))
        if (opt.isDefined) opt
        else {
          try2Opt(Try(Class.forName(implicitly[ClassTag[T]].runtimeClass.getCanonicalName).asInstanceOf[Class[I]]))
        }
      }
    }
  }
}

trait Test {
  def result: String
}