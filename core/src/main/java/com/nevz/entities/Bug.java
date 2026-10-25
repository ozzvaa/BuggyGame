package com.nevz.entities;

import static com.nevz.config.GameConfig.REVEAL_FADE_TIME;
import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.nevz.config.GameConfig;

public class Bug extends Entity{
    private final TextureRegion textureRegion;
    private final float SPEED = GameConfig.BUG_SPEED;
    private Vector2 direction = new Vector2();
    private float sonarAlpha = 0f;
    private float nearbyAlpha = 0f;

    private float revealHoldTimer = 0f;
    private int lastLocatePulseId = -1;

    public Bug(float x, float y, float width, float height, TextureRegion textureRegion) {
        super(x, y, width, height);
        this.textureRegion = textureRegion;
        setOriginCenter();

        direction.set(MathUtils.random(-1,1), MathUtils.random(-1,1)).nor();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (sonarAlpha <= 0f && nearbyAlpha <= 0) {
            return;
        }

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

        // povrne se prejšnja barva batcha
        batch.setColor(oldR, oldG, oldB, oldA);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

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

        // Movement
        fly(delta);

    }

    private void fly(float delta) {
        direction.rotateDeg(MathUtils.random(-10f, 10f));
        setRotation(direction.angleDeg()-90);
        moveBy(direction.x * SPEED * delta, direction.y * SPEED * delta);
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
