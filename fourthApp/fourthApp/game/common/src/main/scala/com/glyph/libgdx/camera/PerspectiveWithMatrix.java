package com.glyph.libgdx.camera;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;

public class PerspectiveWithMatrix extends PerspectiveCamera {
    private LinkedList<Matrix4> mViewMatrixStack;

    public PerspectiveWithMatrix(float fov, float width, float height) {
        super(fov, width, height);
        mViewMatrixStack = new LinkedList<Matrix4>();
    }

    public void pushMatrix() {
        mViewMatrixStack.push(combined.cpy());
    }

    public void popMatrix() {
        combined.set(mViewMatrixStack.pop());
    }
}
