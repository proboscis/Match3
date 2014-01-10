package com.glyph.scala.test;

import com.badlogic.gdx.graphics.GLTexture;

/**
 * @author glyph
 */
public class GLTextureWrapper extends GLTexture{
    public GLTextureWrapper(){
        super(0);
        throw new UnsupportedOperationException("this class cannot be instantiated for use as a texture");
    }
    @Override
    public int getWidth() {
        throw new UnsupportedOperationException("this class cannot be instantiated for use as a texture");
    }

    @Override
    public int getHeight() {
        throw new UnsupportedOperationException("this class cannot be instantiated for use as a texture");
    }

    @Override
    public int getDepth() {
        throw new UnsupportedOperationException("this class cannot be instantiated for use as a texture");
    }

    @Override
    public boolean isManaged() {
        throw new UnsupportedOperationException("this class cannot be instantiated for use as a texture");
    }

    @Override
    protected void reload() {
        throw new UnsupportedOperationException("this class cannot be instantiated for use as a texture");
    }
    public static int createGLHandle(){
        return GLTexture.createGLHandle();
    }
}
