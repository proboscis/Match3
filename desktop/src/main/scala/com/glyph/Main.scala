package com.glyph

import _root_.java.io.File
import com.badlogic.gdx.backends.lwjgl._
import com.badlogic.gdx.tools.imagepacker.TexturePacker2
import com.glyph.scala.lib.util.reactive.RFile
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import scalaz._
import Scalaz._
import com.glyph.scala.lib.libgdx.screen.{ScreenBuilder}
import com.glyph.scala.lib.libgdx.game.ScreenTester
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.glyph.scala.game.action_puzzle.screen.ActionScreen
import com.glyph.scala.lib.libgdx.screen.ScreenBuilder.ScreenConfig

object Main {
  val actionScreenConfig = ScreenConfig(classOf[ActionScreen],Set(classOf[Texture]->Array(
    "data/dummy.png",
    "data/particle.png",
    "data/sword.png"),
    classOf[Skin]->Array("skin/default.json")))
  println(ScreenBuilder.writeConfig(actionScreenConfig))
  //TODO どうやってスクリーンを決定するかね
  case class Config(screenFile: String = "screens/action.js", resDir: File = new File(""), fileCheck: Boolean = false, packTexture: Boolean = false)

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

      help("help") text "prints this message"
    }

    parser.parse(args, Config()) map {
      case Config(screenFileName, resDir, fileCheck, packTexture) => {
        GdxFile.absResourceDir = {
          val result = resDir.getAbsolutePath
          println("specified resource directory:" + resDir + "=>" + result)
          result.some
        }
        val builder = ScreenBuilder.createFromJson(resDir.getAbsolutePath+"/"+screenFileName)
        builder match {
          case Success(b) =>{
            if (fileCheck) RFile.enableChecking(1000)
            if (packTexture) TexturePacker2.process("./", "./skin", "default")
            val cfg = new LwjglApplicationConfiguration()
            cfg.title = "Game"
            val ratio = 9d / 16d
            val height = 1920 / 3
            cfg.height = height
            cfg.width = (height * ratio).toInt
            cfg.useGL20 = true
            new LwjglApplication(new ScreenTester(b), cfg)
          }
          case Failure(errors) =>  errors foreach(_.printStackTrace())
        }
      }
    }
  }
}