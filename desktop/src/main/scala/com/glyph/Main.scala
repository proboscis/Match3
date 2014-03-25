package com.glyph

import _root_.java.io.File
import _root_.scala.util.Try
import com.badlogic.gdx.backends.lwjgl._
import com.glyph._scala.lib.util.reactive.RFile
import com.glyph._scala.lib.libgdx.reactive.GdxFile
import scalaz._
import Scalaz._
import com.glyph._scala.lib.libgdx.screen.ScreenBuilder
import com.glyph._scala.lib.libgdx.game.{ConfiguredGame, ScreenTester}
import com.glyph._scala.test.TestRunner
import com.badlogic.gdx.{Gdx, Game}
import com.glyph._scala.lib.libgdx.game.ApplicationConfig

object Main {

  case class Config(
                     screenFile: String = "screens/action.js",
                     resDir: File = new File(""/*"../android/assets/"*/),
                     fileCheck: Boolean = false,
                     testScreen: String = "",
                     gameClass: String = "",
                     height: Int = 1920/3,
                     width: Int = (1920 * 9d / 16d/3).toInt)

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

      opt[File]('r', "res_dir") action {
        (file, config) => config.copy(resDir = file)
      } text "resources are read from specified directory. available in desktop build only"

      opt[String]('s', "screen") action {
        (fileName, config) => config.copy(screenFile = fileName)
      } text "specify the json file of the screen config"

      opt[String]('t', "test") action {
        (t, config) => config.copy(testScreen = t)
      } text "specify the name of test screen"
      opt[String]('g', "game_class") action {
        (t, config) => config.copy(gameClass = t)
      } text "specify the class name of the game"
      opt[Int]('h', "height") action {
        (h, config) => config.copy(height = h)
      } text "resolution of height"
      opt[Int]('w', "width") action {
        (w, config) => config.copy(width = w)
      } text "resolution of width"
      help("help") text "prints this message"
    }

    parser.parse(args, Config()) map {
      case Config(
      screenFileName,
      resDir,
      fileCheck,
      testScreen,
      gameClass,
      height,
      width) => {
        val resourceDir = resDir.getAbsolutePath
        GdxFile.absResourceDir = {
          println("specified resource directory:" + resDir + "=>" + resourceDir)
          resourceDir.some
        }
        if (fileCheck) RFile.enableChecking(1000)

        val cfg = new LwjglApplicationConfiguration()
        cfg.title = "Game"
        cfg.height = height
        cfg.width = width
        Try(Class.forName(gameClass)) match {
          case scala.util.Success(s) => {
            (s.newInstance() match {
              case game: ConfiguredGame => {
                val ApplicationConfig(width, height) = game.deskTopConfig
                cfg.width = width
                cfg.height = height
                game
              }
              case game => game
            }).asInstanceOf[Game] |> (new LwjglApplication(_, cfg))
          }
          case scala.util.Failure(f) =>
            testScreen match {
              case "" => new LwjglApplication(new TestRunner, cfg) //TODO abstract this class or setGame
              case _ => new LwjglApplication(new TestRunner(testScreen)(()=>{}), cfg)
              case _ => new LwjglApplication(new ScreenTester(testScreen), cfg)
            }
        }
      }
    }
  }
}