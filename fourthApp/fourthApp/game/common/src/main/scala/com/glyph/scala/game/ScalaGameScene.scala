package com.glyph.scala.game
import adapter.RendererAdapter
import com.glyph.game.GameScene
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.Glyph
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener, Actor}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.Texture
import system.RenderSystem

/**
 * a gamescene written in scala
 */
class ScalaGameScene(x: Int, y: Int) extends GameScene(x, y) {
  val game = new GameContext
  /**
   * initializer
   */
  Glyph.printExecTime("init",{

    game.entityContainer.addAdapter[RendererAdapter]
    for (i <- 1 to 1000) {
      game.entityContainer.addEntity(EntityFactory.createNewCharacter)
    }
    game.entityContainer.addEntity(EntityFactory.dungeon)

    /**
     * event manager test
     */
    game.eventManager += callback
    def callback(i:Int)={
      Glyph.log("handle")
      true
    }
    game.eventManager <= 3
    mGameSurface.add(new RenderSystem(game.entityContainer))
  })
  val testActor = new Actor(){
    val sprite = new Sprite(AM.instance().get[Texture]("data/card1.png"))
    this.setWidth(100)
    this.setHeight(100)
    this.setX(-50)
    this.setRotation(45)
    this.setOrigin(getWidth/2,getHeight/2)

    override def draw(batch:SpriteBatch,alpha:Float){
      super.draw(batch,alpha)
      sprite.setOrigin(getOriginX,getOriginY)
      sprite.setPosition(this.getX,this.getY)
      sprite.setSize(getWidth,getHeight)
      sprite.setRotation(this.getRotation)
      sprite.draw(batch,alpha)
    }
  }
  testActor.addListener(new InputListener(){
    override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = {
      val result = super.touchDown(event, x, y, pointer, button)
      Glyph.log("touch!")
      result
    }
  })
  //mGameSurface.addActor(testActor)

  override def render(delta: Float) {
    super.render(delta)
    game.entityContainer.update()
  }
}
