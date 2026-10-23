package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.nevz.config.GameConfig;

public class Witch extends Entity implements Pool.Poolable {
    private final float SPEED = GameConfig.WITCH_SPEED;
    private Vector2 direction = new Vector2();

    public Witch() {
        super(0, 0, GameConfig.WITCH_SIZE, GameConfig.WITCH_SIZE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        moveBy(
            direction.x * delta * SPEED,
            direction.y * delta * SPEED
        );
    }

    public void spawn(Vector2 batPosition) {
        int side = MathUtils.random(0, 1);
        switch (side) {
            case 0:
                setPosition(
                    batPosition.x - GameConfig.WORLD_WIDTH,
                    batPosition.y - MathUtils.random(batPosition.y - GameConfig.WORLD_HEIGHT / 2, batPosition.y + GameConfig.WORLD_HEIGHT / 2));
                direction.set(batPosition.sub(getCenter()).nor());
                break;
            case 1:
                setPosition(
                    batPosition.x + GameConfig.WORLD_WIDTH,
                    batPosition.y - MathUtils.random(batPosition.y - GameConfig.WORLD_HEIGHT / 2, batPosition.y + GameConfig.WORLD_HEIGHT / 2));
                direction.set(batPosition.sub(getCenter()).nor());
                break;
        }
    }

    @Override
    public void reset() {
    }

    public boolean doRemove(Vector2 batPosition) {
        return getCenter().dst2(batPosition) > GameConfig.WORLD_WIDTH * GameConfig.WORLD_WIDTH * 2;
    }
}
