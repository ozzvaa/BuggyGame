package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.nevz.config.GameConfig;

public class Bat extends Entity {
    private final float SPEED = GameConfig.BAT_SPEED;
    private TextureRegion textureRegion;
    private Vector2 velocity = new Vector2(0, 0);
    private Vector2 target = new Vector2();
    private Vector2 toTarget = new Vector2(0, 0);
    private Vector2 direction = new Vector2(1, 0);


    public Bat(TextureRegion textureRegion) {
        super(
            GameConfig.WORLD_WIDTH / 2,
            GameConfig.WORLD_HEIGHT / 2,
            GameConfig.BAT_SIZE,
            GameConfig.BAT_SIZE
        );
        this.textureRegion = textureRegion;
        target.set(getCenter());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        followTarget();
        moveBy(velocity.x * delta, velocity.y * delta);
    }

    public void setTarget(Vector2 target) {
        this.target.set(target);
    }

    private void followTarget() {
        toTarget.set(target.cpy().sub(getCenter()));

        if (toTarget.len2() < 1) {
            direction.set(0, 0);
        } else {
            direction.set(toTarget.cpy().nor());
        }

        velocity.set(direction.cpy().scl(SPEED));
    }


}
