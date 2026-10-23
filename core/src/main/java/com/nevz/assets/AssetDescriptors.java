package com.nevz.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> ATLAS =
        new AssetDescriptor<TextureAtlas>(AssetPaths.ATLAS, TextureAtlas.class);


    private AssetDescriptors() {
    }
}
