package com.nevz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nevz.assets.AssetDescriptors;
import com.nevz.screens.GameScreen;
import com.nevz.screens.MainMenuScreen;
import com.nevz.screens.TreeScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class BuggyGame extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    private TextureAtlas gameAtlas;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();

        // Assets
        assetManager.load(AssetDescriptors.ATLAS);


        assetManager.load(AssetDescriptors.BAT_SHEET);
        assetManager.finishLoading();

        assetManager.finishLoading();

        gameAtlas = assetManager.get(AssetDescriptors.ATLAS);

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
        gameAtlas.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {return shapeRenderer;}

    public AssetManager getAssetManager() {return assetManager;}

    public TextureAtlas getGameAtlas() {return gameAtlas;}
}
