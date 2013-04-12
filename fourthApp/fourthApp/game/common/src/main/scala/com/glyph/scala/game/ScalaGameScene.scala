package com.glyph.scala.game
import adapter.RendererAdapter
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.glyph.scala.Glyph
import com.badlogic.gdx.scenes.scene2d.{Stage, InputEvent, InputListener, Actor}
import com.glyph.libgdx.asset.AM
import com.badlogic.gdx.graphics.{FPSLogger, Texture}
import system.RenderSystem
import com.glyph.libgdx.{Scene, Engine}
import com.glyph.libgdx.surface.Surface
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Skin, Table}
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle

/**
 * a gamescene written in scala
 */
class ScalaGameScene(x: Int, y: Int) extends Scene(x, y) {
  val game = new GameContext
  val mFpsLogger = new FPSLogger
  val mGameTable = new Table
  val mGameSurface = new Surface(Engine.VIRTUAL_WIDTH,Engine.VIRTUAL_HEIGHT / 2)
  val mGameStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT,true)
  val mUIStage = new Stage(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT,true)

  /**
   * initializer
   */
  Glyph.printExecTime("init",{
    /**
     * init processors
     */
    addProcessor(mGameStage)
    addProcessor(mUIStage)
    addStage(mGameStage)
    addStage(mUIStage)

    /**
     * init skins
     */
    val skin = new Skin();
    val ra = AM.instance().get[Texture]("data/rightArrow.png");
    val la = AM.instance().get[Texture]("data/leftArrow.png");
    val exe = AM.instance().get[Texture]("data/lightbulb32.png");
    skin.add("right", ra);
    skin.add("left", la);
    skin.add("exec", exe);
    val bstyle = new ButtonStyle();
    bstyle.up = skin.getDrawable("right");
    skin.add("default", bstyle);

    /**
     * init buttons
     */
    val right = new Button(skin.getDrawable("right"));

    val left = new Button(skin.getDrawable("left"));

    val exec = new Button(skin.getDrawable("exec"));
    val t = mGameTable;
    t.setSize(Engine.VIRTUAL_WIDTH, Engine.VIRTUAL_HEIGHT);
    t.row();
    t.add(mGameSurface).expandX().height(Engine.VIRTUAL_HEIGHT / 2).fill()
      .colspan(3);
    t.row();
    t.add().colspan(3).expand(1, 3);
    t.row();
    t.add(left).expand(1, 1);
    t.add(exec).expand(1, 1);
    t.add(right).expand(1, 1);
    t.debug();
    mUIStage.addActor(t);
    mGameTable.layout();
    /**
     * init entities
     */
    game.entityContainer.addAdapter[RendererAdapter]
    for (i <- 1 to 1000) {
      game.entityContainer.addEntity(EntityFactory.createNewCharacter(game))
    }
    game.entityContainer.addEntity(EntityFactory.dungeon(game))

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
  mGameSurface.addActor(testActor)

  override def render(delta: Float) {
    super.render(delta)
    mFpsLogger.log();
    Table.drawDebug(mGameStage);
    Table.drawDebug(mUIStage);
    mGameSurface.resize()
    game.entityContainer.update()
  }
}
