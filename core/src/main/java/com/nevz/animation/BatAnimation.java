package com.nevz.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class BatAnimation {

    private static final int FRAME_COLS = 14;
    private static final int FRAME_ROWS = 1;
    private static final float FRAME_DURATION = 0.07f;

    private BatAnimation() {
        // Utility class
    }

    public static Animation<TextureRegion> createBatAnimation(
        Texture batSheet) {

        TextureRegion[][] split = TextureRegion.split(
            batSheet,
            batSheet.getWidth() / FRAME_COLS,
            batSheet.getHeight() / FRAME_ROWS
        );

        Array<TextureRegion> frames = new Array<>();

        for (int row = 0; row < FRAME_ROWS; row++) {
            for (int col = 0; col < 7; col++) {
                frames.add(split[row][col]);
            }
        }

        return new Animation<>(
            FRAME_DURATION,
            frames,
            Animation.PlayMode.LOOP
        );
    }
}
