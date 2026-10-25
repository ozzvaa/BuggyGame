package com.nevz.entities;

import static com.nevz.config.GameConfig.REVEAL_FADE_TIME;
import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class StatusBar extends Entity {
    private static final float OUTLINE_THICKNESS = 2f;

    private float percentage;
    private Color color;
    private final Color outlineColor = Color.GRAY;

    private float sonarAlpha = 0f;
    private float nearbyAlpha = 0f;
    private float revealHoldTimer = 0f;
    private int lastLocatePulseId = -1;

    private float screenX;
    private float screenY;
    private float screenW;
    private float screenH;

    public StatusBar(float x, float y, float width, float height, Color color) {
        screenX = x;
        screenY = y;
        screenW = width;
        screenH = height;
        this.color = new Color(color);
        this.percentage = 100f;
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

    }

    public void setPercentage(float percentage) {
        this.percentage = Math.max(0f, Math.min(100f, percentage));
    }

    public float getPercentage() {
        return percentage;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public void render(ShapeRenderer shapeRenderer) {
        float outline = OUTLINE_THICKNESS;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Outline
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, sonarAlpha);
        shapeRenderer.rect(screenX, screenY, screenW, screenH);

        // Filled portion
        shapeRenderer.setColor(color.r, color.g, color.b, sonarAlpha);
        shapeRenderer.rect(
            screenX + outline,
            screenY + outline,
            (screenW - outline * 2f) * (percentage / 100f),
            screenH - outline * 2f
        );
        shapeRenderer.end();
    }

    public Vector2 getStageCenter() {
        return getStage().screenToStageCoordinates(getCenter());
    }

    public void revealFromLocate(int pulseId) {
        // Isti sonar drevesa ne razkrije ponovno vsak frame.
        if (lastLocatePulseId == pulseId) {
            return;
        }

        lastLocatePulseId = pulseId;
        sonarAlpha = 1f;
        revealHoldTimer = REVEAL_HOLD_TIME;
    }
}
