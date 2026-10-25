package com.nevz;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.nevz.assets.AssetDescriptors;
import com.nevz.screens.MainMenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class BuggyGame extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    private TextureAtlas gameAtlas;
    private Sound echoSound;
    private Sound attackSound;
    private Sound gameOverSound;
    private Sound sleepSound;
    private Music ambientMusic;


    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new AssetManager();

        // Assets
        assetManager.load(AssetDescriptors.ATLAS);
        assetManager.load(AssetDescriptors.ECHO_SOUND);
        assetManager.load(AssetDescriptors.ATTACK_SOUND);
        assetManager.load(AssetDescriptors.AMBIENT_MUSIC);
        assetManager.load(AssetDescriptors.GAME_OVER_SOUND);
        assetManager.load(AssetDescriptors.SLEEP_SOUND);
        assetManager.load(AssetDescriptors.BAT_SHEET);

        assetManager.finishLoading();

        echoSound = assetManager.get(AssetDescriptors.ECHO_SOUND);
        attackSound = assetManager.get(AssetDescriptors.ATTACK_SOUND);
        gameOverSound = assetManager.get(AssetDescriptors.GAME_OVER_SOUND);
        ambientMusic = assetManager.get(AssetDescriptors.AMBIENT_MUSIC);
        gameAtlas = assetManager.get(AssetDescriptors.ATLAS);
        sleepSound = assetManager.get(AssetDescriptors.SLEEP_SOUND);

        ambientMusic.setLooping(true);
        ambientMusic.setVolume(0.4f);
        ambientMusic.play();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        assetManager.dispose();
        gameAtlas.dispose();
    }

    public void playEchoSound() {
        if (echoSound != null) {
            echoSound.play(0.8f);
        }
    }

    public void playAttackSound() {
        if (attackSound != null) {
            attackSound.play(0.6f);
        }
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureAtlas getGameAtlas() {
        return gameAtlas;
    }
}
