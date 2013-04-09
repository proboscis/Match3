package com.glyph.libgdx;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Scene extends InputMultiplexer implements Screen {
    private LinkedList<Stage> mStages;

    private int mWidth;
    private int mHeight;

    public Scene(int width, int height) {
        mStages = new LinkedList<Stage>();
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        for (Stage l : mStages) {
            l.act(delta);
        }

        for (Stage l : mStages) {
            l.draw();
        }

    }

    @Override
    public void resize(int width, int height) {
        for (Stage s : mStages) {
            s.setViewport(mWidth, mHeight, true);
            s.getCamera().translate(-s.getGutterWidth(), -s.getGutterHeight(), 0);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    public void addStage(Stage l) {
        mStages.add(l);
    }

    public void removeStage(Stage l) {
        mStages.remove(l);
    }
}
