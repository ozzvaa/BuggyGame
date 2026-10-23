package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nevz.config.GameConfig;

public class Bat extends Entity {
    private TextureRegion textureRegion;

    public Bat(TextureRegion textureRegion) {
        super(
            GameConfig.WORLD_WIDTH / 2,
            GameConfig.WORLD_HEIGHT / 2,
            GameConfig.BAT_SIZE,
            GameConfig.BAT_SIZE
        );
        this.textureRegion = textureRegion;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}
