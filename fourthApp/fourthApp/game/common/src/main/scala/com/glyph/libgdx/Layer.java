package com.glyph.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * ビューポート設定によるクリッピングに対応したステージ
 * レイヤーとして扱う
 *
 * @author glyph
 */
public class Layer extends Stage {
    private Scene mScene;
    private int mViewportX;
    private int mViewportY;
    private int mViewportWidth;
    private int mViewportHeight;

    public Layer(int w, int h, Scene parent) {
        super(w, h, true);
        mScene = parent;
        mViewportX = 0;
        mViewportY = 0;
        mViewportWidth = w;
        mViewportHeight = h;
    }

    public Scene getScene() {
        return mScene;
    }

    /**
     * setViewport params in virtual windows coords.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param vw virtual screen width
     * @param vh virtual screen height
     */
    public void setViewport(int x, int y, int width, int height, int vWidth, int vHeight) {
        super.setViewport(width, height, false);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float offsetX, offsetY;
        float targetH, targetW;
        float ratio = (float) vHeight / (float) vWidth;
        if (screenHeight / screenWidth > ratio) {
            targetH = screenWidth * ratio;
            targetW = screenWidth;
            offsetY = (screenHeight - targetH) / 2f;
            offsetX = 0;
        } else {
            targetH = screenHeight;
            targetW = screenHeight / ratio;
            offsetX = (screenWidth - targetW) / 2f;
            offsetY = 0;
        }
        mViewportX = (int) (offsetX + (float) x / (float) vWidth * targetW);
        mViewportY = (int) (offsetY + (float) y / (float) vHeight * targetH);
        mViewportWidth = (int) (width * (targetW / (float) vWidth));
        mViewportHeight = (int) (height * (targetH / (float) vHeight));

        Camera camera = getCamera();
        camera.viewportWidth = vWidth + offsetX * (vWidth / targetW) * 2;
        camera.viewportHeight = vHeight + offsetY * (vHeight / targetH) * 2;
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

    }

    public void setViewport(int x, int y, int width, int height) {
        super.setViewport(width, height, true);
        mViewportX = x;
        mViewportY = y;
        mViewportWidth = width;
        mViewportHeight = height;
    }

    @Override
    public void draw() {
        //Gdx.gl.glViewport(mViewportX, mViewportY, mViewportWidth, mViewportHeight);
        //Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
        super.draw();
    }

    @Override
    public Vector2 screenToStageCoordinates(Vector2 screenCoords) {
        //		getCamera().unproject(Vector3.tmp.set(screenCoords.x, screenCoords.y, 0), mViewportX, mViewportY,
        //				mViewportWidth, mViewportHeight);
        //		screenCoords.x = Vector3.tmp.x;
        //		screenCoords.y = Vector3.tmp.y;
        //		return screenCoords;
        return super.screenToStageCoordinates(screenCoords);
    }

    @Override
    public Vector2 stageToScreenCoordinates(Vector2 stageCoords) {
        //		getCamera().project(Vector3.tmp.set(stageCoords.x, stageCoords.y, 0), mViewportX, mViewportY, mViewportWidth,
        //				mViewportHeight);
        //		stageCoords.x = Vector3.tmp.x;
        //		stageCoords.y = Vector3.tmp.y;
        //		return stageCoords;
        return super.stageToScreenCoordinates(stageCoords);
    }

    @Override
    public float getWidth() {
        return mViewportWidth;
    }

    @Override
    public float getHeight() {
        return mViewportHeight;
    }
}
