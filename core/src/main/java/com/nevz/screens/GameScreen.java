package com.nevz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nevz.BuggyGame;
import com.nevz.assets.RegionNames;
import com.nevz.config.GameConfig;
import com.nevz.entities.Bat;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen extends ScreenAdapter {
    private final BuggyGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    private Bat bat;

    public GameScreen(BuggyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FillViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.getBatch());

        // Game elements
        bat = new Bat(game.getGameAtlas().findRegion(RegionNames.BAT));
        stage.addActor(bat);

        setupInputs();
//        stage.setDebugAll(true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.17f, 0.16f, 0.02f, 0);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void setupInputs() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                Vector2 pos = stage.screenToStageCoordinates(
                    new Vector2(screenX, screenY)
                );

                bat.setTarget(pos);
                return false;
            }
        });

        Gdx.input.setInputProcessor(multiplexer);
    }
}
