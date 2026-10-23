package com.nevz.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nevz.config.GameConfig;
import com.nevz.utils.Utils;

import static com.nevz.config.GameConfig.REVEAL_FADE_TIME;
import static com.nevz.config.GameConfig.REVEAL_HOLD_TIME;

public class Tree extends Entity {
    private final TextureRegion textureRegion;
    private final int chunkId;

    private float sonarAlpha = 0f;
    private float revealHoldTimer = 0f;
    private int lastLocatePulseId = -1;

    public Tree(float x, float y, TextureRegion textureRegion) {
        super(x, y, GameConfig.TREE_WIDTH, GameConfig.TREE_HEIGHT);
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
        revealHoldTimer = REVEAL_HOLD_TIME;
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (textureRegion == null || sonarAlpha <= 0f) {
            return;
        }

        Color oldBatchColor = batch.getColor().cpy();
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

        batch.setColor(oldBatchColor);
    }

    public int getChunkId() {
        return chunkId;
    }

    public float getSonarAlpha() {
        return sonarAlpha;
    }
}


