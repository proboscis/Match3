package com.glyph.libgdx.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.Pool;

import java.util.LinkedList;

/**
 * @author glyph
 */
public class GLUtil {
    private static GLUtil sInstance = new GLUtil();
    private LinkedList<State> mStateStack = new LinkedList<State>();
    private State mCurrent;
    private StatePool mPool = new StatePool();

    public static GLUtil instance(){
        return sInstance;
    }
    private GLUtil(){
        mCurrent = mPool.obtain();
    }

    public void save(){
        mStateStack.push(mCurrent);
        mCurrent = mPool.obtain();
    }

    public void glScissor(int x, int y, int width, int height){
        Gdx.gl.glScissor(x,y,width,height);
        int[] s = mCurrent.scissor;
        s[0] = x;
        s[1] = y;
        s[2] = width;
        s[3] = height;
    }
    public void setScissor(boolean b){
        mCurrent.scissorEnabled = b;
        if(mCurrent.scissorEnabled){
            Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
        }else{
            Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
        }
    }


    public void restore(){
        mPool.free(mCurrent);
        mCurrent = mStateStack.pop();

        if(mCurrent.scissorEnabled){
            Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
        }else{
            Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
        }

        int[] s = mCurrent.scissor;
        Gdx.gl.glScissor(s[0],s[1],s[2],s[3]);
    }

    static class State{
        int[] scissor = new int[4];
        boolean scissorEnabled = false;
    }

    static class StatePool extends Pool<State> {
        @Override
        protected State newObject() {
            return new State();
        }
    }
}
