package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.nevz.config.GameConfig;

public class Bat extends Entity {
    private final float SPEED = GameConfig.BAT_SPEED;

    private final Animation<TextureRegion> animation;
    private final TextureRegion perchTexture;
    private boolean perchingTo = false;
    private float stateTime = 0f;

    private final Vector2 velocity = new Vector2(0, 0);

    private final Vector2 perchTarget = new Vector2();
    private final Vector2 target = new Vector2();
    private final Vector2 toTarget = new Vector2(0, 0);
    private final Vector2 direction = new Vector2(1, 0);

    public Bat(Animation<TextureRegion> animation, TextureRegion perchTexture) {
        super(
            GameConfig.WORLD_WIDTH / 2f,
            GameConfig.WORLD_HEIGHT / 2f,
            GameConfig.BAT_SIZE,
            GameConfig.BAT_SIZE
        );

        this.animation = animation;
        this.perchTexture = perchTexture;

        setOriginCenter();
        target.set(getCenter());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        TextureRegion currentFrame =
            animation.getKeyFrame(stateTime);

        if (isPerchingTo()) {
            currentFrame = perchTexture;
        }

        batch.draw(
            currentFrame,
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

    @Override
    public void act(float delta) {
        super.act(delta);

        stateTime += delta;

        if (perchingTo) {
            followPerch();
        }
        else {
            followTarget();
        }

        moveBy(
            velocity.x * delta * SPEED,
            velocity.y * delta * SPEED
        );
    }

    public void setTarget(Vector2 target) {
        this.target.set(target);
    }

    private void followTarget() {
        toTarget.set(target.cpy().sub(getCenter()));

        if (toTarget.len() < 0.1) {
            direction.set(0, 0);
        } else {
            direction.set(toTarget.cpy().nor());
        }

        if (direction.x < 0) {
            setScaleX(-1f);  // vodoravno zrcaljenje
        } else if (direction.x > 0) {
            setScaleX(1f);
        }

        velocity.set(direction.cpy().scl(SPEED));
    }

    private void followPerch() {
        toTarget.set(perchTarget.cpy().sub(getCenter()));

        if (toTarget.len() < 0.1) {
            direction.set(0, 0);
        } else {
            direction.set(toTarget.cpy().nor());
        }

        if (direction.x < 0) {
            setScaleX(-1f);  // vodoravno zrcaljenje
        } else if (direction.x > 0) {
            setScaleX(1f);
        }

        velocity.set(direction.cpy().scl(SPEED));
    }

    public Vector2 getAimDirection(Vector2 out) {
        out.set(target).sub(getCenter());

        if (out.len2() < 0.0001f) {
            out.set(1f, 0f);
        } else {
            out.nor();
        }

        return out;
    }

    public void perchAtPoint(Vector2 perch) {
        if (perch != null) {
            perchTarget.set(perch);
            perchingTo = true;
        }
        else {
            perchingTo = false;
        }
    }

    public boolean isPerchingTo() {
        return perchingTo && velocity.isZero();
    }

    public void unperch() {
        perchingTo = false;
    }
}
