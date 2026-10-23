package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.nevz.config.GameConfig;

import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME;

public class Witch extends Entity implements Pool.Poolable {
    private final float SPEED = GameConfig.WITCH_SPEED;
    private Vector2 direction = new Vector2();

    private Animation<TextureRegion> animation;
    private float stateTime = 0f;

    private float sonarAlpha = 0f;
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
        if (animation == null) {
            return;
        }

        TextureRegion currentFrame =
            animation.getKeyFrame(stateTime);

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

        if (direction.x < 0) {
            setScaleX(-1f); //levo
        } else if (direction.x > 0) {
            setScaleX(1f);
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
}
