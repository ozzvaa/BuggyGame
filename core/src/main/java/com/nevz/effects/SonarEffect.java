package com.nevz.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class SonarEffect implements Disposable {

    private static final int ARC_COUNT = 6;
    private static final int SEGMENTS = 48;

    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final Vector2 origin = new Vector2();
    private final Vector2 direction = new Vector2(1f, 0f);

    private float elapsed = 0f;
    private float maxRange;
    private float duration;
    private float halfAngle;

    private boolean active = false;

    public void start(
        Vector2 origin,
        Vector2 direction,
        float maxRange,
        float duration,
        float halfAngle
    ) {
        this.origin.set(origin);
        this.direction.set(direction);

        if (this.direction.isZero()) {
            this.direction.set(1f, 0f);
        } else {
            this.direction.nor();
        }

        this.maxRange = maxRange;
        this.duration = duration;
        this.halfAngle = halfAngle;

        elapsed = 0f;
        active = maxRange > 0f && duration > 0f;
    }

    public void update(float delta) {
        if (!active) {
            return;
        }

        elapsed += delta;

        if (elapsed >= duration) {
            elapsed = duration;
            active = false;
        }
    }

    public void draw(Matrix4 projectionMatrix) {
        if (!active || maxRange <= 0f || duration <= 0f) {
            return;
        }

        float progress = MathUtils.clamp(
            elapsed / duration,
            0f,
            1f
        );

        float currentRange = maxRange * progress;
        float spacing = maxRange / ARC_COUNT;

        float startAngle = direction.angleDeg() - halfAngle;
        float arcAngle = halfAngle * 2f;

        shapeRenderer.setProjectionMatrix(projectionMatrix);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < ARC_COUNT; i++) {
            float radius = currentRange - i * spacing;

            if (radius <= 0f) {
                continue;
            }

            float distanceProgress = radius / maxRange;
            float alpha = 0.8f * (1f - distanceProgress);
            alpha = MathUtils.clamp(alpha, 0f, 0.8f);

            shapeRenderer.setColor(
                1f,
                1f,
                0f,
                alpha
            );

            drawCustomArc(origin.x, origin.y, radius, startAngle, arcAngle);
        }

        shapeRenderer.end();
    }


    private void drawCustomArc(float x, float y, float radius, float startAngle, float degrees) {
        float angleStep = degrees / SonarEffect.SEGMENTS;

        float prevX = x + radius * MathUtils.cosDeg(startAngle);
        float prevY = y + radius * MathUtils.sinDeg(startAngle);

        for (int i = 1; i <= SonarEffect.SEGMENTS; i++) {
            float currentAngle = startAngle + i * angleStep;
            float nextX = x + radius * MathUtils.cosDeg(currentAngle);
            float nextY = y + radius * MathUtils.sinDeg(currentAngle);

            shapeRenderer.line(prevX, prevY, nextX, nextY);

            prevX = nextX;
            prevY = nextY;
        }
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
