package com.nevz.utils;

import static com.nevz.config.GameConfig.CHUNK_SIZE;

import com.badlogic.gdx.math.Vector2;

public final class Utils {
    private Utils() {
        // Prevent instantiation
    }

    public static int getChunk(float position) {
        return (int) Math.floor(position / CHUNK_SIZE);
    }

    public static Vector2 getChunk(float x, float y) {
        return new Vector2(getChunk(x), getChunk(y));
    }

    public static Vector2 getChunk(Vector2 position) {
        return getChunk(position.x, position.y);
    }

}
