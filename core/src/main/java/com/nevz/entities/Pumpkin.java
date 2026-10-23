package com.nevz.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nevz.config.GameConfig;

import static com.nevz.config.GameConfig.REVEAL_FADE_TIME;
import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME;

public class Pumpkin extends Entity {

    private final TextureRegion textureRegion;

    private float velocityY = 0f;
    private boolean landed = false;

    private float sonarAlpha = 1f;
    private float revealHoldTimer = REVEAL_HOLD_TIME;
    private int lastLocatePulseId = -1;

    public Pumpkin(
        float x,
        float y,
        TextureRegion textureRegion) {

        super(
            x,
            y,
            GameConfig.PUMPKIN_WIDTH,
            GameConfig.PUMPKIN_HEIGHT
        );

        this.textureRegion = textureRegion;
        setOriginCenter();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // pada dokler ne pride do tal
        if (!landed) {
            velocityY -= GameConfig.PUMPKIN_GRAVITY * delta;
            moveBy(0f, velocityY * delta);

            if (getY() <= 0f) {
                setY(0f);
                velocityY = 0f;
                landed = true;

                // ob pristanku začne bledeti
                revealHoldTimer = REVEAL_HOLD_TIME;
            }

            return;
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
    }

    public void revealFromLocate(int pulseId) {
        if (lastLocatePulseId == pulseId) {
            return;
        }

        lastLocatePulseId = pulseId;
        sonarAlpha = 1f;
        revealHoldTimer = REVEAL_HOLD_TIME;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textureRegion == null || sonarAlpha <= 0f) {
            return;
        }

        Color oldColor = batch.getColor().cpy();
        Color actorColor = getColor();

        batch.setColor(
            actorColor.r,
            actorColor.g,
            actorColor.b,
            actorColor.a * parentAlpha * sonarAlpha
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

        batch.setColor(oldColor);
    }

    public float getSonarAlpha() {
        return sonarAlpha;
    }
}
