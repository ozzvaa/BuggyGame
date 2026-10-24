package com.nevz.entities;

import static com.nevz.config.GameConfig.PERCH_RADIUS;
import static com.nevz.config.GameConfig.REVEAL_FADE_TIME_TREE;
import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME_TREE;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nevz.utils.Utils;


public class Tree extends Entity {
    private final TextureRegion textureRegion;
    private final int chunkId;

    private float sonarAlpha = 0f;
    private float nearbyAlpha = 0f;
    private float revealHoldTimer = 0f;
    private int lastLocatePulseId = -1;

    private Array<Vector2> perchPoints = new Array<>(); // Are in coordinates relative to Tree Entity Actor

    public Tree(float x, float y, float width, float height, TextureRegion textureRegion) {
        super(x, y, width, height);
        this.textureRegion = textureRegion;
        this.chunkId = Utils.getChunk(x);

    }

    public void revealFromLocate(int pulseId) {
        // Isti sonar drevesa ne razkrije ponovno vsak frame.
        if (lastLocatePulseId == pulseId) {
            return;
        }

        lastLocatePulseId = pulseId;
        sonarAlpha = 1f;
        revealHoldTimer = REVEAL_HOLD_TIME_TREE;
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
                sonarAlpha - delta / REVEAL_FADE_TIME_TREE
            );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textureRegion == null || sonarAlpha <= 0f && nearbyAlpha <= 0) {
            return;
        }

        float alpha = Math.max(nearbyAlpha, sonarAlpha);

        Color oldBatchColor = batch.getColor().cpy();
        Color actorColor = getColor();

        batch.setColor(
            actorColor.r,
            actorColor.g,
            actorColor.b,
            actorColor.a * parentAlpha * alpha
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

        batch.setColor(oldBatchColor);
    }

    public void debugSetVisible() {
        sonarAlpha = 1;
    }

    public void debugAddPerchPoint(Vector2 point) {
        perchPoints.add(point);
    }

    public void debugRemovePerchPoint(Vector2 point) {
        Vector2 closestPoint = getClosestPerchPoint(point);
        if (closestPoint == null) return;

        perchPoints.removeValue(closestPoint, true);

    }

    public Vector2 getClosestPerchPoint(Vector2 point) {
        float closestDist = -1;
        Vector2 closestPoint = null;
        for (int i = perchPoints.size - 1; i >= 0; i--) {
            Vector2 perch = perchPoints.get(i);
            float dstToPerch = perch.dst(point);
            if (closestDist == -1 || dstToPerch < closestDist) {
                closestPoint = perch;
                closestDist = dstToPerch;
            }
        }
        if (closestPoint == null || closestDist > PERCH_RADIUS) return null;
        return closestPoint;
    }

    public Array<Vector2> getPerchPoints() {
        return perchPoints;
    }

    public void setPerchPoints(Array<Vector2> points) {
        perchPoints.clear();
        for (Vector2 point : points) {
            perchPoints.add(point.cpy());
        }
    }

    public int getChunkId() {
        return chunkId;
    }

    public float getSonarAlpha() {
        return sonarAlpha;
    }

    public void setNearbyAlpha(float alpha) {
        nearbyAlpha = alpha;
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
        for (Vector2 point : perchPoints) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.GREEN);

            Vector2 worldPoint = localToStageCoordinates(point.cpy());

            shapes.circle(worldPoint.x, worldPoint.y, PERCH_RADIUS, 20);
        }
    }
}


