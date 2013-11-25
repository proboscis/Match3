import java.awt.event.KeyEvent
import java.awt.Robot
import javafx.scene.input.KeyCode
import scalaz._
import Scalaz._

/**
 * @author glyph
 */
object RobotTest {
  def main (args: Array[String]) {
    new Thread(new Runnable{
      def run(){
        val robot = new Robot
        while(true){
          Thread.sleep(1000)
          robot.keyPress(KeyEvent.VK_A)
        }
      }
    }).run()
  }
}
