package com.nevz.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nevz.BuggyGame;
import com.nevz.animation.BatAnimation;
import com.nevz.animation.WitchAnimation;
import com.nevz.assets.AssetDescriptors;
import com.nevz.config.GameConfig;
import com.nevz.entities.Bat;
import com.nevz.entities.Tree;
import com.nevz.entities.Witch;
import com.nevz.utils.Utils;

import static com.nevz.config.GameConfig.*;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen extends ScreenAdapter {

    private final BuggyGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private AssetManager assetManager;
    private Animation<TextureRegion> witchAnimation;

    private boolean locateActive = false;
    private float locateElapsed = 0f;
    private float locateCooldownTimer = 0f;
    private float locateCurrentRange = 0f;
    private int locatePulseId = 0;

    private final Vector2 locateOrigin = new Vector2();
    private final Vector2 locateDirection = new Vector2(1f, 0f);
    private final Vector2 candidateOffset = new Vector2();

    // Bat
    private Bat bat;

    // Witches
    private Pool<Witch> witchPool = new Pool<Witch>() {
        @Override
        protected Witch newObject() {
            return new Witch();
        }
    };

    private Array<Witch> witches = new Array<>();

    private Integer witchCount = 0;
    private float witchTimer = 0;

    // Trees
    private IntMap<Tree> chunkTrees = new IntMap<>();

    public GameScreen(BuggyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        assetManager = game.getAssetManager();

        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.getBatch());

        TextureAtlas atlas =
            assetManager.get(AssetDescriptors.ATLAS);

        Texture batSheet =
            assetManager.get(AssetDescriptors.BAT_SHEET);

        Animation<TextureRegion> batAnimation =
            BatAnimation.createBatAnimation(batSheet);

        witchAnimation =
            WitchAnimation.createWitchAnimation(atlas);

        bat = new Bat(batAnimation);
        stage.addActor(bat);

        setupInputs();
        stage.setDebugAll(true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0);

        gameLoop(delta);
        stage.act(delta);
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

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    activateLocate();
                    return true;
                }

                return false;
            }
        });

        Gdx.input.setInputProcessor(multiplexer);
    }

    private void gameLoop(float delta) {
        updateLocate(delta);
        handleWitches(delta);
        handleTrees();
    }

    // Witches
    private void handleWitches(float delta) {
        // Reset far witches to pool
        for (int i = witches.size - 1; i >= 0; i--) {
            Witch witch = witches.get(i);
            if (witch.doRemove(bat.getCenter())) {
                witch.remove();
                witchPool.free(witch);
                witches.removeIndex(i);
                witchCount--;
            }
        }

        // Spawn new witch every 10 seconds
        witchTimer += delta;

        if (witchTimer < GameConfig.WITCH_TIMER) {
            return;
        }

        witchTimer = 0;
        spawnWithces();


    }

    private void spawnWithces() {
        if (witchCount >= 10) return;

        Witch witch = witchPool.obtain();
        witch.setAnimation(witchAnimation);
        witch.spawn(bat.getCenter());
        stage.addActor(witch);
        witches.add(witch);
        witchCount++;
    }

    // Trees
    private void handleTrees() {
        int chunk = Utils.getChunk(bat.getCenterX());

        int chunk_span = GameConfig.CHUNK_SPAN_X;

        for (int i = chunk - chunk_span; i < chunk + chunk_span; i++) {
            if (!chunkTrees.containsKey(i)) {
                spawnTree(i);
            }
        }
    }

    private void spawnTree(int chunkId) {
        int chance = MathUtils.random(0, 2);
        if (chance == 2) {
            Tree tree = new Tree(
                MathUtils.random(chunkId * GameConfig.CHUNK_SIZE, (chunkId + 1) * GameConfig.CHUNK_SIZE),
                0
            );
            chunkTrees.put(chunkId, tree);
            stage.addActor(tree);
        } else {
            chunkTrees.put(chunkId, null);
        }
    }

    // Echolocation
    private void activateLocate() {
        if (locateCooldownTimer > 0f || locateActive) {
            return;
        }

        locateActive = true;
        locateElapsed = 0f;
        locateCurrentRange = 0f;
        locateCooldownTimer = LOCATE_COOLDOWN;

        locateOrigin.set(bat.getCenterX(), bat.getCenterY());
        bat.getAimDirection(locateDirection);

        locatePulseId++; // vsak strel svoj ID
    }

    private void updateLocate(float delta) {
        if (locateCooldownTimer > 0f) {
            locateCooldownTimer = Math.max(
                0f,
                locateCooldownTimer - delta
            );
        }

        if (!locateActive) {
            return;
        }

        locateElapsed += delta;

        float progress = Math.min(
            1f,
            locateElapsed / LOCATE_DURATION
        );

        locateCurrentRange = LOCATE_MAX_RANGE * progress;

        revealWitchesReachedByPulse();

        if (progress >= 1f) {
            locateActive = false;
        }
    }

    private void revealWitchesReachedByPulse() {
        float rangeSquared =
            locateCurrentRange * locateCurrentRange;

        float minDot =
            MathUtils.cosDeg(LOCATE_HALF_ANGLE);

        for (Witch witch : witches) {
            candidateOffset.set(
                witch.getCenterX() - locateOrigin.x,
                witch.getCenterY() - locateOrigin.y
            );

            float distanceSquared = candidateOffset.len2();

            // Val čarovnice še ni dosegel.
            if (distanceSquared > rangeSquared) {
                continue;
            }

            // Če čarovnica ni tik ob izvoru, preveri kot.
            if (distanceSquared > 0.0001f) {
                candidateOffset.nor();

                if (candidateOffset.dot(locateDirection) < minDot) {
                    continue;
                }
            }

            // Val jo je dosegel.
            witch.revealFromLocate(locatePulseId);
        }
    }

}
