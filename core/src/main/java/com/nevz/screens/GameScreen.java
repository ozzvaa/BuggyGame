package com.nevz.screens;

import static com.nevz.config.GameConfig.CHUNK_SIZE;
import static com.nevz.config.GameConfig.LOCATE_COOLDOWN;
import static com.nevz.config.GameConfig.LOCATE_DURATION;
import static com.nevz.config.GameConfig.LOCATE_HALF_ANGLE;
import static com.nevz.config.GameConfig.LOCATE_MAX_RANGE;
import static com.nevz.config.GameConfig.TREE_WIDTH;
import static com.nevz.config.GameConfig.WORLD_HEIGHT;
import static com.nevz.config.GameConfig.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nevz.BuggyGame;
import com.nevz.animation.BatAnimation;
import com.nevz.animation.WitchAnimation;
import com.nevz.assets.AssetDescriptors;
import com.nevz.assets.RegionNames;
import com.nevz.config.GameConfig;
import com.nevz.effects.SonarEffect;
import com.nevz.entities.Bat;
import com.nevz.entities.Fireball;
import com.nevz.entities.Pumpkin;
import com.nevz.entities.Tree;
import com.nevz.entities.Witch;
import com.nevz.utils.TreesData;
import com.nevz.utils.Utils;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen extends ScreenAdapter {

    private final BuggyGame game;
    private final Array<TextureRegion> treeRegions = new Array<>();
    private final Array<TextureRegion> pumpkinRegions = new Array<>();
    private final Array<Fireball> fireballs = new Array<>();
    private final Array<Pumpkin> pumpkins = new Array<>();
    private final Vector2 locateOrigin = new Vector2();
    private final Vector2 locateDirection = new Vector2(1f, 0f);
    private final Vector2 candidateOffset = new Vector2();
    // Witches
    private final Pool<Witch> witchPool = new Pool<Witch>() {
        @Override
        protected Witch newObject() {
            return new Witch();
        }
    };
    private final Array<Witch> witches = new Array<>();
    // Trees
    private final IntMap<Tree> chunkTrees = new IntMap<>();
    private FitViewport viewport;
    private OrthographicCamera camera;
    private Stage stage;
    private Animation<TextureRegion> witchAnimation;
    private TreesData treesData;
    private TextureRegion fireballRegion;
    private boolean locateActive = false;
    private float locateElapsed = 0f;
    private float locateCooldownTimer = 0f;
    private float locateCurrentRange = 0f;
    private int locatePulseId = 0;
    private float attackCooldownTimer = 0f;
    private SonarEffect sonarEffect;
    // Bat
    private Bat bat;
    private Integer witchCount = 0;
    private float witchTimer = 0;

    public GameScreen(BuggyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        AssetManager assetManager = game.getAssetManager();

        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.getBatch());

        TextureAtlas atlas =
            assetManager.get(AssetDescriptors.ATLAS);

        treeRegions.clear();
        treeRegions.addAll(atlas.findRegions(RegionNames.TREE));
        loadTreeData();

        fireballRegion = atlas.findRegion(RegionNames.FIREBALL);
        pumpkinRegions.addAll(atlas.findRegions(RegionNames.PUMPKIN));

        Texture batSheet =
            assetManager.get(AssetDescriptors.BAT_SHEET);

        Animation<TextureRegion> batAnimation =
            BatAnimation.createBatAnimation(batSheet);

        witchAnimation =
            WitchAnimation.createWitchAnimation(atlas);

        sonarEffect = new SonarEffect();

        bat = new Bat(batAnimation, atlas.findRegion(RegionNames.BAT_PERCH));
        bat.toFront();

        stage.addActor(bat);

        setupInputs();
        stage.setDebugAll(false);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        handleCamera(delta);
        gameLoop(delta);

        stage.act(delta);
        stage.draw();
//        drawDebug();

        if (sonarEffect.isActive()) {
            sonarEffect.setOrigin(bat.getCenter());
        }

        sonarEffect.draw(camera.combined);
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(bat.getCenter(), 0);
        camera.update();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (sonarEffect != null) {
            sonarEffect.dispose();
        }
    }

    private void setupInputs() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    if (bat.isPerching()) {
                        bat.unperch();
                    }
                    return true;
                }
                return false;
            }

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

    private void drawDebug() {
        ShapeRenderer sr = game.getShapeRenderer();
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(0, 0, WORLD_WIDTH, 0);
        sr.line(0, 0, 0, WORLD_HEIGHT);

        sr.end();
    }

    private void handleCamera(float delta) {
        Vector2 screenMiddle = new Vector2(
            Gdx.graphics.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f
        );

        Vector2 batScreen = stage.stageToScreenCoordinates(bat.getCenter());
        Vector2 cameraToBat = batScreen.sub(screenMiddle);

        float distance = cameraToBat.len();
        float maxCameraDistance = screenMiddle.y / 2f;

        if (distance > maxCameraDistance) {
            float correction = distance - maxCameraDistance;
            Vector2 cameraCorrection = cameraToBat.nor().scl(correction);

            float smoothness = 1f - (float) Math.exp(-5f * delta);

            camera.position.add(
                cameraCorrection.x * smoothness,
                -cameraCorrection.y * smoothness,
                0
            );
        }

        camera.position.y = Math.max(camera.viewportHeight / 2f, camera.position.y);

        camera.update();
    }

    private void gameLoop(float delta) {
        updateAttackCooldown(delta);
        updateLocate(delta);
        handleWitches(delta);
        handleTrees();
        handleFireballCollisions();
        revealNearby();

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
//        if (witchCount >= 10) return;

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
                bat.toFront();
            }
        }
    }

    private void spawnTree(int chunkId) {
        int chance = MathUtils.random(0, 2);

        float x = MathUtils.random(
            chunkId * GameConfig.CHUNK_SIZE,
            (chunkId + 1) * GameConfig.CHUNK_SIZE
        );

        if (chunkId == -1 || chunkId == 0 || chunkId == 1) {
            x = chunkId * CHUNK_SIZE + CHUNK_SIZE / 2f;
        }

        if (chance == 2 || chunkId == -1 || chunkId == 0 || chunkId == 1) {
            int randomTreeIndex = MathUtils.random(0, treesData.trees.size - 1);
            TextureRegion tr = treeRegions.get(randomTreeIndex);

            float width = tr.getRegionWidth();
            float height = tr.getRegionHeight();
            float scale = TREE_WIDTH / Math.min(width, height);
            width *= scale;
            height *= scale;

            Tree tree = new Tree(x, 0f, width, height, tr);
            tree.setPerchPoints(treesData.trees.get(randomTreeIndex).perchPoints);
            tree.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (button == Input.Buttons.LEFT) {
                        System.out.println("Clicked");

                        Vector2 closestPoint = tree.getClosestPerchPoint(new Vector2(x, y));
                        if (closestPoint == null) return false;

                        closestPoint = tree.localToStageCoordinates(closestPoint.cpy());
                        bat.perchAtPoint(closestPoint);
                        return true;
                    }
                    return false;
                }
            });

            chunkTrees.put(chunkId, tree);
            stage.addActor(tree);
        } else {
            chunkTrees.put(chunkId, null);
        }
    }

    private void loadTreeData() {
        FileHandle file = Gdx.files.local("trees.json");

        if (!file.exists()) return;

        Json json = new Json();
        treesData = json.fromJson(TreesData.class, file);

        Gdx.app.log("TreeScreen", "Trees loaded from " + file.path());
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
        sonarEffect.start(
            locateOrigin,
            locateDirection,
            LOCATE_MAX_RANGE,
            LOCATE_DURATION,
            LOCATE_HALF_ANGLE
        );

        locatePulseId++; // vsak strel svoj ID
    }

    private void updateLocate(float delta) {
        sonarEffect.update(delta);

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

    private void revealNearby() {
        // Trees
        int chunkId = Utils.getChunk(bat.getCenter().x);
        for (int i = chunkId - 1; i < chunkId + 2; i++) {
            Tree nearTree = chunkTrees.get(i);
            if (nearTree != null) {

                float minDistance = nearTree.getWidth();

                float visibilityPercent = Utils.calculateNearbyVisibility(
                    bat.getCenter(),
                    nearTree.getCenter(),
                    minDistance / 2f
                );

                nearTree.setNearbyAlpha(visibilityPercent);
            }
        }

        for (Witch witch : witches) {
            if (witch.getNearbyAlpha() <= 0 && (witch.getCenter().dst2(bat.getCenter()) > witch.getHeight() * witch.getHeight() * 5))
                continue; // Optimisation to not check every witch every time

            float minDistance = witch.getWidth() * 1.5f;
            float visibilityPercent = Utils.calculateNearbyVisibility(
                bat.getCenter(),
                witch.getCenter(),
                minDistance / 2f
            );

            witch.setNearbyAlpha(visibilityPercent);

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
