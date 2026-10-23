package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.nevz.config.GameConfig;

public class Fireball extends Entity {

    private final TextureRegion textureRegion;
    private final Vector2 direction = new Vector2();

    private float lifeTime = GameConfig.FIREBALL_LIFETIME;

    public Fireball(
        float centerX,
        float centerY,
        Vector2 direction,
        TextureRegion textureRegion) {

        super(
            centerX - GameConfig.FIREBALL_SIZE / 2f,
            centerY - GameConfig.FIREBALL_SIZE / 2f,
            GameConfig.FIREBALL_SIZE,
            GameConfig.FIREBALL_SIZE
        );

        this.textureRegion = textureRegion;
        this.direction.set(direction).nor();

        setOriginCenter();
        setRotation(this.direction.angleDeg());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        lifeTime -= delta;

        moveBy(
            direction.x * GameConfig.FIREBALL_SPEED * delta,
            direction.y * GameConfig.FIREBALL_SPEED * delta
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(
            textureRegion,
            getX(),
            getY(),
            getOriginX(),
            getOriginY(),
            getWidth(),
            getHeight(),
            getScaleX(),
            getScaleY(),
            getRotation()
        );
    }

    public boolean isExpired() {
        return lifeTime <= 0f;
    }
}
