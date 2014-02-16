package com.glyph._scala.test

import com.badlogic.gdx.{Gdx, Game}
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.graphics.{Texture, GL10}
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.{ChangeListener, TextureRegionDrawable}
import scala.List
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener
import com.badlogic.gdx.Input.Keys

class SkinTest extends Game {
  val listEntries = Array[Object]("This is a list entry", "And another one", "The meaning of life", "Is hard to come by",
    "This is a list entry", "And another one", "The meaning of life", "Is hard to come by", "This is a list entry",
    "And another one", "The meaning of life", "Is hard to come by", "This is a list entry", "And another one",
    "The meaning of life", "Is hard to come by", "This is a list entry", "And another one", "The meaning of life",
    "Is hard to come by")

  lazy val skin = new Skin(Gdx.files.internal("skin/holo/Holo-dark-xhdpi.json"))
  lazy val stage = new Stage(Gdx.graphics.getWidth, Gdx.graphics.getHeight, false)
  lazy val texture1 = new Texture(Gdx.files.internal("data/sword.png"))
  lazy val texture2 = new Texture(Gdx.files.internal("data/sword.png"))
  lazy val fpsLabel =  new Label("fps:", skin)

  override def create(): Unit = {
    val image = new TextureRegion(texture1)
    val imageFlipped = new TextureRegion(image)
    imageFlipped.flip(true, true)
    val image2 = new TextureRegion(texture2)
    Gdx.input.setInputProcessor(stage)

    // Group.debug = true;

    val style = new ImageButtonStyle(skin.get(classOf[ButtonStyle]))
    style.imageUp = new TextureRegionDrawable(image)
    style.imageDown = new TextureRegionDrawable(imageFlipped)
    val iconButton = new ImageButton(style)

    val buttonMulti = new TextButton("Multi\nLine\nToggle", skin, "toggle")
    val imgButton = new Button(new Image(image), skin)
    val imgToggleButton = new Button(new Image(image), skin, "toggle")

    val myLabel = new Label("this is some text.", skin)
    myLabel.setWrap(true)

    val t = new Table()
    t.row()
    t.add(myLabel)

    t.layout()

    val checkBox = new CheckBox("Check me", skin)
    val slider = new Slider(0, 10, 1, false, skin)
    val textfield = new TextField("", skin)
    textfield.setMessageText("Click here!")
    val dropdown = new SelectBox(Array[Object]("Android", "Windows", "Linux", "OSX", "Android", "Windows", "Linux",
      "OSX", "Android", "Windows", "Linux", "OSX", "Android", "Windows", "Linux", "OSX", "Android", "Windows", "Linux", "OSX",
      "Android", "Windows", "Linux", "OSX", "Android", "Windows", "Linux", "OSX"), skin)
    val imageActor = new Image(image2)
    val scrollPane = new ScrollPane(imageActor)
    val list = new com.badlogic.gdx.scenes.scene2d.ui.List(listEntries, skin)
    val scrollPane2 = new ScrollPane(list, skin)
    scrollPane2.setFlickScroll(false)
    val splitPane = new SplitPane(scrollPane, scrollPane2, false, skin, "default-horizontal")
    // configures an example of a TextField in password mode.
    val passwordLabel = new Label("Textfield in password mode: ", skin)
    val passwordTextField = new TextField("", skin)
    passwordTextField.setMessageText("password")
    passwordTextField.setPasswordCharacter('*')
    passwordTextField.setPasswordMode(true)

    // window.debug();
    val window = new Window("Dialog", skin)
    window.getButtonTable().add(new TextButton("X", skin)).height(window.getPadTop())
    window.setPosition(0, 0)
    window.defaults().spaceBottom(10)
    window.row().fill().expandX()
    window.add(iconButton)
    window.add(buttonMulti)
    window.add(imgButton)
    window.add(imgToggleButton)
    window.row()
    window.add(checkBox)
    window.add(slider).minWidth(100).fillX().colspan(3)
    window.row()
    window.add(dropdown)
    window.add(textfield).minWidth(100).expandX().fillX().colspan(3)
    window.row()
    window.add(splitPane).fill().expand().colspan(4).maxHeight(200)
    window.row()
    window.add(passwordLabel).colspan(2)
    window.add(passwordTextField).minWidth(100).expandX().fillX().colspan(2)
    window.row()
    window.add(fpsLabel).colspan(4)
    window.pack()

    // stage.addActor(new Button("Behind Window", skin));
    stage.addActor(window)

    textfield.setTextFieldListener(new TextFieldListener() {
      override def keyTyped(textField:TextField,key:Char){
        if (key == '\n') textField.getOnscreenKeyboard().show(false)
      }
    })

    slider.addListener(new ChangeListener() {
      override def changed(event:ChangeEvent,actor:Actor){
        Gdx.app.log("UITest", "slider: " + slider.getValue())
      }
    })

    iconButton.addListener(new ChangeListener() {
      override def changed(event:ChangeEvent,actor:Actor){
        new Dialog("Some Dialog", skin, "dialog") {
          override def result(obj:Object){
            println("Chosen: " + obj)
          }
        }.text("Are you enjoying this demo?").button("Yes", true).button("No", false).key(Keys.ENTER, true)
          .key(Keys.ESCAPE, false).show(stage)
      }
    })
  }
  override def dispose(): Unit = {
    super.dispose()

    stage.dispose();
    skin.dispose();
    texture1.dispose();
    texture2.dispose();
  }

  override def resize(width: Int, height: Int): Unit = {
    super.resize(width, height)
    stage.setViewport(width, height, false);
  }

  override def render(): Unit = {
    super.render()
    Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());

    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
    Table.drawDebug(stage);
  }
}
