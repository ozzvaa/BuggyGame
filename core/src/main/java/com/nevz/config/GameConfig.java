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

    public static final float BAT_SIZE = 5 * UNIT;
    public static final float BAT_SPEED = 4 * UNIT;

    public static final float WITCH_SIZE = 4 * UNIT;
    public static final float WITCH_SPEED = 3 * UNIT;
    public static final float WITCH_TIMER = 3;

    public static final float REVEAL_HOLD_TIME = 0.35f;
    public static final float REVEAL_FADE_TIME = 1.0f;

    public static final float LOCATE_COOLDOWN = 2f;
    public static final float LOCATE_DURATION = 0.7f;
    public static final float LOCATE_MAX_RANGE =
        GameConfig.WORLD_WIDTH * 0.8f;
    public static final float LOCATE_HALF_ANGLE = 20f;

    public static final float TREE_WIDTH = 20 * UNIT;
    public static final float TREE_HEIGHT = 20 * UNIT;
    public static final float PERCH_RADIUS = 2 * UNIT;


    public static final float ATTACK_COOLDOWN = 5f;

    public static final float FIREBALL_SPEED = 150f;
    public static final float FIREBALL_SIZE = 3 * UNIT;
    public static final float FIREBALL_LIFETIME = 3f;

    public static final float PUMPKIN_WIDTH = 2 * UNIT;
    public static final float PUMPKIN_HEIGHT = 2 * UNIT;
    public static final float PUMPKIN_GRAVITY = 100f;
}
