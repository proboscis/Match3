package com.glyph

import _root_.java.io.File
import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx.tools.imagepacker.TexturePacker2
import com.glyph.scala.lib.util.reactive.RFile
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder
import com.glyph.scala.lib.libgdx.game.{ScreenTester, ScreenFileTester}
import com.glyph.scala.test.TestRunner

object Main {
  case class Config(screenFile: String = "screens/action.js", resDir: File = new File("../common/src/main/resources/"), fileCheck: Boolean = false, packTexture: Boolean = false, testScreen: String = "")
  implicit object ScoptClass extends scopt.Read[Class[_ <: ScreenBuilder]] {
    def arity: Int = 1
    def reads: (String) => Class[_ <: ScreenBuilder] = clsName => Class.forName(clsName, false, ClassLoader.getSystemClassLoader).asInstanceOf[Class[_ <: ScreenBuilder]]
  }

  def main(args: Array[String]) {
    val parser = new scopt.OptionParser[Config]("game") {
      head("game", " currently in beta")
      opt[Unit]('f', "file_check") action {
        (_, c) => c.copy(fileCheck = true)
      } text "enables the file checker "

      opt[Unit]('p', "pack_texture") action {
        (_, c) =>
          c.copy(packTexture = true)
      } text "pack all textures if set to true"

      opt[File]('r', "res_dir") action {
        (file, config) => config.copy(resDir = file)
      } text "resources are read from specified directory. available in desktop build only"

      opt[String]('s', "screen") action {
        (fileName, config) => config.copy(screenFile = fileName)
      } text "specify the json file of the screen config"

      opt[String]('t', "test") action {
        (t, config) => config.copy(testScreen = t)
      } text "specify the name of test screen"


      help("help") text "prints this message"
    }

    parser.parse(args, Config()) map {
      case Config(screenFileName, resDir, fileCheck, packTexture, testScreen) => {
        val resourceDir = resDir.getAbsolutePath
        GdxFile.absResourceDir = {
          println("specified resource directory:" + resDir + "=>" + resourceDir)
          resourceDir.some
        }
        if (fileCheck) RFile.enableChecking(1000)

        if (packTexture) {
          val setting = new TexturePacker2.Settings()
          setting.maxWidth = 2048
          setting.maxHeight = 2048
          TexturePacker2.process(setting,resourceDir,"./skin", "default")
        }
        val cfg = new LwjglApplicationConfiguration()
        cfg.title = "Game"
        val ratio = 9d / 16d
        val height = 1920 / 3
        cfg.height = height
        cfg.width = (height * ratio).toInt
        cfg.useGL20 = true
        testScreen match {
          case "" => new LwjglApplication(new TestRunner, cfg)
          case "" => new LwjglApplication(new ScreenFileTester(screenFileName), cfg)
          case _ => new LwjglApplication(new TestRunner(testScreen), cfg)
          case _ => new LwjglApplication(new ScreenTester(testScreen), cfg)
        }
      }
    }
  }
}