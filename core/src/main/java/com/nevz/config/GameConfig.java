package com.nevz.config;

public class GameConfig {
    public static final float UNIT = 1;
    public static final float WORLD_WIDTH = 80;
    public static final float WORLD_HEIGHT = 40;

    public static final float BAT_SIZE = 5 * UNIT;
    public static final float BAT_SPEED = 5 * UNIT;

    public static final float WITCH_SIZE = 4 * UNIT;
    public static final float WITCH_SPEED = 5 * UNIT;
    public static final float WITCH_TIMER = 2;

    public static final float REVEAL_HOLD_TIME = 0.35f;
    public static final float REVEAL_FADE_TIME = 1.0f;

    public static final float LOCATE_COOLDOWN = 2f;
    public static final float LOCATE_DURATION = 0.7f;
    public static final float LOCATE_MAX_RANGE =
        GameConfig.WORLD_WIDTH * 0.8f;
    public static final float LOCATE_HALF_ANGLE = 20f;


}
