package com.nevz.config;

public class GameConfig {
    public static final float UNIT = 1;
    public static final float WORLD_WIDTH = 80;
    public static final float WORLD_HEIGHT = 40;

    public static final float CHUNK_SIZE = 40;
    public static final int CHUNK_SPAN_X =
        (int) Math.ceil(WORLD_WIDTH / CHUNK_SIZE);

    public static final int CHUNK_SPAN_Y =
        (int) Math.ceil(WORLD_HEIGHT / CHUNK_SIZE);

    public static final float BAT_SIZE = 3 * UNIT;
    public static final float BAT_SPEED = 15 * UNIT;

    public static final float WITCH_SIZE = 5 * UNIT;
    public static final float WITCH_SPEED = 30 * UNIT;
    public static final float WITCH_TIMER = 1;

    public static final float TREE_WIDTH = 5 * UNIT;
    public static final float TREE_HEIGHT = 15 * UNIT;


}
