package com.glyph

import _root_.java.io.File
import com.badlogic.gdx.backends.lwjgl._
import com.glyph.scala.DebugGame
import com.badlogic.gdx.tools.imagepacker.TexturePacker2
import com.glyph.scala.lib.util.reactive.RFile
import com.glyph.scala.lib.libgdx.reactive.GdxFile
import scalaz._
import Scalaz._
object Main {
  case class Config(resDir:File = new File(""),fileCheck:Boolean = false, packTexture: Boolean = false)

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
        (file,config)=> config.copy(resDir = file)
      } text "resources are read from specified directory. available in desktop build only"

      help("help") text "prints this message"
    }

    parser.parse(args, Config()) map {
      case Config(resDir,fileCheck, packTexture) => {
        GdxFile.absResourceDir = {
          val result = resDir.getAbsolutePath
          println("specified resource directory:" + resDir+"=>"+result)
          result.some
        }
        if (fileCheck) RFile.enableChecking(1000)
        if (packTexture) TexturePacker2.process("./", "./skin", "default")
      }
    }
    val cfg = new LwjglApplicationConfiguration()
    cfg.title = "Game"
    val ratio = 9d / 16d
    val height = 1920 / 3
    cfg.height = height
    cfg.width = (height * ratio).toInt
    cfg.useGL20 = true
    new LwjglApplication(new DebugGame(), cfg)
  }
}