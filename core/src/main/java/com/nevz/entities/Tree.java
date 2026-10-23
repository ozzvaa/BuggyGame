package com.nevz.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.nevz.config.GameConfig;
import com.nevz.utils.Utils;

public class Tree extends Entity {
    //    private TextureRegion textureRegion;
    private int chunkId;

    public Tree(float x, float y) {
        super(x, y, GameConfig.TREE_WIDTH, GameConfig.TREE_HEIGHT);
        chunkId = Utils.getChunk(x);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}
