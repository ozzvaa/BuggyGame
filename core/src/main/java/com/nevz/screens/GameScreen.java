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
import com.nevz.assets.RegionNames;
import com.nevz.config.GameConfig;
import com.nevz.entities.*;
import com.nevz.utils.Utils;

import static com.nevz.config.GameConfig.*;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen extends ScreenAdapter {

    private final BuggyGame game;
    private Viewport viewport;
    private Stage stage;
    private Animation<TextureRegion> witchAnimation;
    private final Array<TextureRegion> treeRegions = new Array<>();
    private final Array<TextureRegion> pumpkinRegions = new Array<>();
    private TextureRegion fireballRegion;

    private final Array<Fireball> fireballs = new Array<>();
    private final Array<Pumpkin> pumpkins = new Array<>();

    private boolean locateActive = false;
    private float locateElapsed = 0f;
    private float locateCooldownTimer = 0f;
    private float locateCurrentRange = 0f;
    private int locatePulseId = 0;
    private float attackCooldownTimer = 0f;

    private final Vector2 locateOrigin = new Vector2();
    private final Vector2 locateDirection = new Vector2(1f, 0f);
    private final Vector2 candidateOffset = new Vector2();

    // Bat
    private Bat bat;

    // Witches
    private final Pool<Witch> witchPool = new Pool<Witch>() {
        @Override
        protected Witch newObject() {
            return new Witch();
        }
    };

    private final Array<Witch> witches = new Array<>();

    private Integer witchCount = 0;
    private float witchTimer = 0;

    // Trees
    private final IntMap<Tree> chunkTrees = new IntMap<>();

    public GameScreen(BuggyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        AssetManager assetManager = game.getAssetManager();

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.getBatch());

        TextureAtlas atlas =
            assetManager.get(AssetDescriptors.ATLAS);

        treeRegions.clear();
        treeRegions.addAll(atlas.findRegions(RegionNames.TREE));

        fireballRegion = atlas.findRegion(RegionNames.FIREBALL);
        pumpkinRegions.addAll(atlas.findRegions(RegionNames.PUMPKIN));

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
        handleFireballCollisions();
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

                if (keycode == Input.Keys.A) {
                    tryAttack();
                    return true;
                }

                return false;
            }
        });

        Gdx.input.setInputProcessor(multiplexer);
    }

    private void gameLoop(float delta) {
        updateAttackCooldown(delta);
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
            float x = MathUtils.random(
                chunkId * GameConfig.CHUNK_SIZE,
                (chunkId + 1) * GameConfig.CHUNK_SIZE
            );

            TextureRegion randomTree = treeRegions.random();
            Tree tree = new Tree(x, 0f, randomTree);

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
        revealTreesReachedByPulse();
        revealPumpkinsReachedByPulse();

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

            // Val čarovnice še ni dosegel
            if (distanceSquared > rangeSquared) {
                continue;
            }

            // Če čarovnica ni tik ob izvoru, preveri kot
            if (distanceSquared > 0.0001f) {
                candidateOffset.nor();

                if (candidateOffset.dot(locateDirection) < minDot) {
                    continue;
                }
            }

            // Val jo je dosegel
            witch.revealFromLocate(locatePulseId);
        }
    }

    private void revealTreesReachedByPulse() {
        float rangeSquared =
            locateCurrentRange * locateCurrentRange;

        float minDot =
            MathUtils.cosDeg(LOCATE_HALF_ANGLE);

        for (IntMap.Entry<Tree> entry : chunkTrees.entries()) {
            Tree tree = entry.value;

            // V tem chunku morda ni drevesa
            if (tree == null) {
                continue;
            }

            candidateOffset.set(
                tree.getCenterX() - locateOrigin.x,
                tree.getCenterY() - locateOrigin.y
            );

            float distanceSquared = candidateOffset.len2();

            // Sonar drevesa še ni dosegel
            if (distanceSquared > rangeSquared) {
                continue;
            }

            // Ali je drevo v kotu
            if (distanceSquared > 0.0001f) {
                candidateOffset.nor();

                if (candidateOffset.dot(locateDirection) < minDot) {
                    continue;
                }
            }

            // Sonar dosegel drevo
            tree.revealFromLocate(locatePulseId);
        }
    }

    private void tryAttack() {
        if (attackCooldownTimer > 0f) {
            return;
        }

        Vector2 direction = bat.getAimDirection(new Vector2());
        Vector2 center = bat.getCenter();

        Fireball fireball = new Fireball(
            center.x,
            center.y,
            direction,
            fireballRegion
        );

        fireballs.add(fireball);
        stage.addActor(fireball);

        attackCooldownTimer = GameConfig.ATTACK_COOLDOWN;
    }

    private void updateAttackCooldown(float delta) {
        attackCooldownTimer = Math.max(
            0f,
            attackCooldownTimer - delta
        );
    }

    private void handleFireballCollisions() {
        for (int i = fireballs.size - 1; i >= 0; i--) {
            Fireball fireball = fireballs.get(i);

            if (fireball.isExpired()) {
                fireball.remove();
                fireballs.removeIndex(i);
                continue;
            }

            boolean hit = false;

            for (int j = witches.size - 1; j >= 0; j--) {
                Witch witch = witches.get(j);

                if (!fireball.getRect().overlaps(witch.getRect())) {
                    continue;
                }

                // shrani položaj pred vračanjem čarovnice v Pool
                float pumpkinX =
                    witch.getCenterX() - GameConfig.PUMPKIN_WIDTH / 2f;

                float pumpkinY = witch.getY();

                TextureRegion randomPumpkin = pumpkinRegions.random();

                Pumpkin pumpkin = new Pumpkin(
                    pumpkinX,
                    pumpkinY,
                    randomPumpkin
                );

                pumpkins.add(pumpkin);
                stage.addActor(pumpkin);

                witch.remove();
                witches.removeIndex(j);
                witchPool.free(witch);
                witchCount = Math.max(0, witchCount - 1);

                fireball.remove();
                fireballs.removeIndex(i);

                hit = true;
                break;
            }

            if (hit) {
                continue;
            }
        }
    }

    private void revealPumpkinsReachedByPulse() {
        float rangeSquared =
            locateCurrentRange * locateCurrentRange;

        float minDot =
            MathUtils.cosDeg(LOCATE_HALF_ANGLE);

        for (Pumpkin pumpkin : pumpkins) {
            candidateOffset.set(
                pumpkin.getCenterX() - locateOrigin.x,
                pumpkin.getCenterY() - locateOrigin.y
            );

            float distanceSquared = candidateOffset.len2();

            if (distanceSquared > rangeSquared) {
                continue;
            }

            if (distanceSquared > 0.0001f) {
                candidateOffset.nor();

                if (candidateOffset.dot(locateDirection) < minDot) {
                    continue;
                }
            }

            pumpkin.revealFromLocate(locatePulseId);
        }
    }

}
