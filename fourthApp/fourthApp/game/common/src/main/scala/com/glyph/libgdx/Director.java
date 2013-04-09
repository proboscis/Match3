package com.glyph.libgdx;

import com.badlogic.gdx.Gdx;
import com.glyph.libgdx.asset.AM;
import com.glyph.libgdx.particle.ParticlePool;
import com.glyph.libgdx.particle.SpriteParticle;

public class Director {
    Scene mScene;

    ParticlePool<SpriteParticle> mSpritePool;

    private static Director sInstance;

    private Director() {
        mSpritePool = new ParticlePool<SpriteParticle>(SpriteParticle.class, 1000);
    }

    public ParticlePool<SpriteParticle> getSpritePool() {
        return mSpritePool;
    }

    public static Director instance() {
        return sInstance;
    }

    public static void create() {
        sInstance = new Director();
        AM.create();
    }

    public void render() {
        AM.instance().update();
        if (mScene != null) {
            mScene.render(Gdx.graphics.getDeltaTime());
        }
    }

    public static void dispose() {
        if (sInstance.mScene != null) {
            sInstance.mScene.dispose();
        }
        AM.dispose();
    }

    public Scene getScene() {
        return mScene;
    }

    public void setScene(Scene scene) {
        if (mScene != null)
            mScene.hide();
        mScene = scene;
        if (mScene != null) {
            mScene.show();
            mScene.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        Gdx.input.setInputProcessor(mScene);
    }

    public void resize(int width, int height) {
        if (mScene != null) {
            mScene.resize(width, height);
        }
    }

    public void pause() {
        if (mScene != null) {
            mScene.pause();
        }
    }

    public void resume() {
        if (mScene != null) {
            mScene.pause();
        }
    }

}
