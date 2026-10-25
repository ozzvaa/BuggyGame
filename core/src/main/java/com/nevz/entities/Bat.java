package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.nevz.config.GameConfig;

public class Bat extends Entity {
    private final float MAX_SPEED = GameConfig.BAT_SPEED;

    private final Animation<TextureRegion> animation;
    private final TextureRegion perchTexture;
    private boolean perchingTo = false;
    private float stateTime = 0f;
    private float health = GameConfig.MAX_HEALTH;
    private float stamina = GameConfig.MAX_STAMINA;

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
        setTouchable(Touchable.disabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        TextureRegion currentFrame =
            animation.getKeyFrame(stateTime);

        if (isPerching()) {
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

        if (stamina <= 0) {
            fall(delta);
            return;
        }

        if (perchingTo) {
            followPerch();
        }
        else {
            followTarget();
        }

        fly(delta);

    }

    private void fly(float delta) {
        float speed = calculateSpeed();

        moveBy(
            velocity.x * delta * speed,
            velocity.y * delta * speed
        );

        if (getY() < 0) {
            setY(0);
        }

        if (isPerching()) {
            stamina = Math.min(GameConfig.MAX_STAMINA, stamina + delta * 10f);

        } else {
            stamina -= delta;
        }

    }

    private void fall(float delta) {
        velocity.y -= 5*delta;
        velocity.x = 0;
        if (getY() < 0) {
            setY(0);
            velocity.y *= -0.75f;
        }
        moveBy(
            velocity.x * delta * MAX_SPEED,
            velocity.y * delta * MAX_SPEED
        );
    }

    private float calculateSpeed() {
        if (stamina > 50) return MAX_SPEED;

        if (stamina > 25) {
            return MathUtils.lerp(
                MAX_SPEED,
                MAX_SPEED * 0.75f,
                (50f - stamina) / 25f
            );
        }

        else if (stamina > 0) {
            return MathUtils.lerp(
                MAX_SPEED * 0.5f,
                0f,
                (25f - stamina) / 25f
            );
        }
        return 0;
    }

    public float getStamina() {
        return stamina;
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

        velocity.set(direction.cpy().scl(MAX_SPEED));
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

        velocity.set(direction.cpy().scl(MAX_SPEED));
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

    public boolean isPerching() {
        return perchingTo && velocity.isZero();
    }

    public void unperch() {
        perchingTo = false;
    }

    public void resetStamina() {
        stamina = GameConfig.MAX_STAMINA;
    }
}
