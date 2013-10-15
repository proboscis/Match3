import com.badlogic.gdx.tools.imagepacker.TexturePacker2
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings

/**
 * @author glyph
 */
object TexturePacking {
  def main(args: Array[String]) {
    println("hello")
    //val settings = new Settings
    //settings.maxWidth = 512
    //settings.maxHeight = 512
    TexturePacker2.process("../","../","result")

  }
}
