package com.nevz.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.nevz.config.GameConfig;

import static com.nevz.config.GameConfig.REVEAL_FADE_TIME;
import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME;

public class Witch extends Entity implements Pool.Poolable {
    private final float SPEED = GameConfig.WITCH_SPEED;
    private Vector2 direction = new Vector2();

    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    private float sonarAlpha = 0f;
    private float nearbyAlpha = 0f;

    private float revealHoldTimer = 0f;
    private int lastLocatePulseId = -1;

    public Witch() {
        super(0, 0, GameConfig.WITCH_SIZE, GameConfig.WITCH_SIZE);
        setOriginCenter();
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        this.stateTime = 0f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (animation == null || sonarAlpha <= 0f && nearbyAlpha <= 0) {
            return;
        }

        TextureRegion currentFrame =
            animation.getKeyFrame(stateTime);

        // shrani se trenutna barva batcha
        float oldR = batch.getColor().r;
        float oldG = batch.getColor().g;
        float oldB = batch.getColor().b;
        float oldA = batch.getColor().a;

        Color color = getColor();

        float alpha = Math.max(nearbyAlpha, sonarAlpha);

        batch.setColor(
            color.r,
            color.g,
            color.b,
            color.a * parentAlpha * alpha
        );

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

        // povrne se prejšnja barva batcha
        batch.setColor(oldR, oldG, oldB, oldA);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (direction.x < 0) {
            setScaleX(-1f); //levo
        } else if (direction.x > 0) {
            setScaleX(1f);
        }

        if (revealHoldTimer > 0f) {
            revealHoldTimer = Math.max(
                0f,
                revealHoldTimer - delta
            );
        } else if (sonarAlpha > 0f) {
            sonarAlpha = Math.max(
                0f,
                sonarAlpha - delta / REVEAL_FADE_TIME
            );
        }

        moveBy(
            direction.x * delta * SPEED,
            direction.y * delta * SPEED
        );
    }

    public void spawn(Vector2 batPosition) {
        stateTime = 0f;
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
                    batPosition.y - MathUtils.random(
                        batPosition.y - GameConfig.WORLD_HEIGHT / 2f,
                        batPosition.y + GameConfig.WORLD_HEIGHT / 2f
                    )
                );
                break;
        }
        direction.set(batPosition).sub(getCenter()).nor();
    }

    @Override
    public void reset() {
        stateTime = 0f;
        direction.setZero();
        sonarAlpha = 0f;
        revealHoldTimer = 0f;
        lastLocatePulseId = -1;
        setVisible(true);
        setPosition(0f, 0f);
        setScaleX(1f);
        setScaleY(1f);
    }

    public boolean doRemove(Vector2 batPosition) {
        return getCenter().dst2(batPosition) > GameConfig.WORLD_WIDTH * GameConfig.WORLD_WIDTH * 2;
    }

    public void revealFromLocate(int pulseId) {
        // Isti sonar je ne sme razkriti znova vsak frame
        if (lastLocatePulseId == pulseId) {
            return;
        }

        lastLocatePulseId = pulseId;

        sonarAlpha = 1f;
        revealHoldTimer = REVEAL_HOLD_TIME;
    }

    public void setNearbyAlpha(float alpha) {
        nearbyAlpha = alpha;
    }

    public float getNearbyAlpha() {
        return nearbyAlpha;
    }
}
