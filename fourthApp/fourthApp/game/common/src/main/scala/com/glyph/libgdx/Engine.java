package com.glyph.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.glyph.libgdx.asset.AM;
import com.glyph.scala.game.scene.ScalaGameScene;

public class Engine implements ApplicationListener {
    float mTimer = 0.0f;
    public static final int VIRTUAL_WIDTH = 540;
    public static final int VIRTUAL_HEIGHT = 960;

    @Override
    public void create() {
        Director.create();
        for (int i = 1; i <= 10; i++) {
            AM.instance().load("data/card" + i + ".png", Texture.class);

        }
        AM.instance().load("sound/drawcard.mp3", Sound.class);
        AM.instance().load("sound/gore.wav", Sound.class);
        AM.instance().load("data/background.png", Texture.class);
        AM.instance().load("data/skeleton.png", Texture.class);
        AM.instance().load("data/table.png", Texture.class);
        AM.instance().load("data/tile.png", Texture.class);
        AM.instance().load("data/rightArrow.png", Texture.class);
        AM.instance().load("data/leftArrow.png", Texture.class);
        AM.instance().load("data/lightbulb32.png", Texture.class);

    }

    @Override
    public void render() {
        Director.instance().render();
        mTimer -= Gdx.graphics.getDeltaTime();
        if (AM.instance().update() && mTimer <= 0) {
            if (Director.instance().getScene() == null) {
                Director.instance().setScene(new ScalaGameScene(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
            }
        }
    }

    @Override
    public void dispose() {
        Director.dispose();
    }

    @Override
    public void resize(int width, int height) {
        Director.instance().resize(width, height);
    }

    @Override
    public void pause() {
        Director.instance().pause();
    }

    @Override
    public void resume() {
        Director.instance().resume();
    }

}
