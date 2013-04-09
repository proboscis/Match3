package com.glyph.libgdx.asset;

import com.badlogic.gdx.assets.AssetManager;

public class AM {
    private AssetManager mAssetManager;
    private static AM sInstance;

    private AM() {
        mAssetManager = new AssetManager();
    }

    public static void create() {
        sInstance = new AM();
    }

    public static void dispose() {
        sInstance.mAssetManager.dispose();
        sInstance = null;
    }

    public static AssetManager instance() {
        return sInstance.mAssetManager;
    }

}
