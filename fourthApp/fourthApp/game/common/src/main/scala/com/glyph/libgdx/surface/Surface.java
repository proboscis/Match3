package com.glyph.libgdx.surface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.glyph.libgdx.surface.drawable.SurfaceDrawable;

import java.util.LinkedList;

/**
 * surface to draw game object
 */
public class Surface extends Group {
    private Camera mCamera;
    private SpriteBatch mBatch;

    private LinkedList<SurfaceDrawable> mDrawables;
    private LinkedList<SurfaceDrawable> mRemoveRequests;
    private LinkedList<SurfaceDrawable> mAddRequests;

    private int mViewportX;
    private int mViewportY;
    private int mViewportWidth;
    private int mViewportHeight;

    private int mResX;
    private int mResY;

    public Surface(int resolutionX, int resolutionY) {
        mCamera = new OrthographicCamera();
        mBatch = new SpriteBatch(1000);
        mDrawables = new LinkedList<SurfaceDrawable>();
        mRemoveRequests = new LinkedList<SurfaceDrawable>();
        mAddRequests = new LinkedList<SurfaceDrawable>();
        mResX = resolutionX;
        mResY = resolutionY;
    }

    /**
     * call this method to fit the view-port of this surface<br>
     * this must be called after the stage's render() method has been called<br>
     * because the camera of stage does not update until that is called.
     */
    public void resize() {
        Stage stage = getStage();
        Vector3 leftBottom = new Vector3(getX(), getY(), 0);
        Vector3 rightTop = new Vector3(getX() + getWidth(), getY()
                + getHeight(), 0);
        Camera camera = stage.getCamera();
        // calc display position
        camera.project(leftBottom);
        camera.project(rightTop);
        mViewportX = Math.round(leftBottom.x);
        mViewportY = Math.round(leftBottom.y);
        mViewportWidth = Math.round(rightTop.x - leftBottom.x);
        mViewportHeight = Math.round(rightTop.y - leftBottom.y);
        //		mCamera.viewportWidth = mViewportWidth;
        mCamera.viewportWidth = mResX;
        mCamera.viewportHeight = mResY;
        mCamera.position.set(leftBottom.add(rightTop).div(2));

    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        /**
         * process remove/add request
         */
        processRequest();
        /**
         * start own drawing
         */
        mCamera.update();
        mBatch.setProjectionMatrix(mCamera.projection);
        Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
        {
            // limit the drawing surface
            Gdx.gl.glScissor(mViewportX, mViewportY, mViewportWidth,
                    mViewportHeight);
            Gdx.gl.glViewport(mViewportX, mViewportY, mViewportWidth, mViewportHeight);
            //Gdx.gl.glClearColor(0, 128, 128, 1);
            //Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            mBatch.begin();
            {
                for (SurfaceDrawable drawable : mDrawables) {
                    drawable.draw(mBatch, parentAlpha);
                }
            }
            mBatch.end();
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
        /**
         * end own drawing
         */
        batch.begin();
    }
    private void processRequest() {
        mDrawables.removeAll(mRemoveRequests);
        mDrawables.addAll(mAddRequests);
        mRemoveRequests.clear();
        mAddRequests.clear();
    }

    public void add(SurfaceDrawable drawable) {
        mAddRequests.add(drawable);
    }

    public void remove(SurfaceDrawable drawable) {
        mRemoveRequests.add(drawable);
    }

    public Camera getCamera(){
        return mCamera;
    }
}
