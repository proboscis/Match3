package com.glyph.java.particle;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ParticlePool<T extends Poolable> extends Pool<T> {

    private final Class<T> mType;

    public ParticlePool(Class<T> type, int capacity) {
        super(capacity);
        mType = type;
    }

    @Override
    protected T newObject() {
        try {
            return mType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T obtain() {

        return super.obtain();
    }
}
