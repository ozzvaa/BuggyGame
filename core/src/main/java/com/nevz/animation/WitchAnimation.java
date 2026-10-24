package com.nevz.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.nevz.assets.RegionNames;

public class WitchAnimation {

    private static final float FRAME_DURATION = 0.12f;

    private WitchAnimation() {
        // Utility class
    }

    public static Animation<TextureRegion> createWitchAnimation(
        TextureAtlas atlas) {

        Array<TextureRegion> frames = new Array<>();

        frames.addAll(atlas.findRegions(RegionNames.WITCH));

        if (frames.size != 4) {
            throw new IllegalStateException(
                "Expected 4 pictures, found: "
                    + frames.size
            );
        }

        return new Animation<>(
            FRAME_DURATION,
            frames,
            Animation.PlayMode.LOOP
        );
    }
}
