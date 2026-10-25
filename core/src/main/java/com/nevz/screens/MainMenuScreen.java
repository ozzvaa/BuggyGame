package com.nevz.screens;

import static com.nevz.assets.RegionNames.START_BUTTON;
import static com.nevz.config.GameConfig.LOCATE_COOLDOWN;
import static com.nevz.config.GameConfig.LOCATE_DURATION;
import static com.nevz.config.GameConfig.LOCATE_HALF_ANGLE;
import static com.nevz.config.GameConfig.LOCATE_MAX_RANGE;
import static com.nevz.config.GameConfig.WORLD_HEIGHT;
import static com.nevz.config.GameConfig.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nevz.BuggyGame;
import com.nevz.animation.BatAnimation;
import com.nevz.assets.AssetDescriptors;
import com.nevz.assets.RegionNames;
import com.nevz.config.GameConfig;
import com.nevz.effects.SonarEffect;
import com.nevz.entities.Bat;
import com.nevz.entities.Fireball;

import com.badlogic.gdx.utils.Array;

public class MainMenuScreen extends ScreenAdapter {

    private final BuggyGame game;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private Stage stage;

    private Bat bat;
    private StartButtonActor startButton;

    private TextureRegion fireballRegion;
    private SonarEffect sonarEffect;

    private final Array<Fireball> fireballs = new Array<>();

    // Sonar
    private boolean locateActive = false;
    private float locateElapsed = 0f;
    private float locateCooldownTimer = 0f;
    private float locateCurrentRange = 0f;

    private final Vector2 locateOrigin = new Vector2();
    private final Vector2 locateDirection = new Vector2(1f, 0f);
    private final Vector2 candidateOffset = new Vector2();

    // Attack
    private float attackCooldownTimer = 0f;

    private final Rectangle startButtonBounds = new Rectangle();

    private boolean startGameRequested = false;

    public MainMenuScreen(BuggyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        AssetManager assetManager = game.getAssetManager();

        camera = new OrthographicCamera();
        viewport = new FitViewport(
            WORLD_WIDTH,
            WORLD_HEIGHT,
            camera
        );

        camera.position.set(
            WORLD_WIDTH / 2f,
            WORLD_HEIGHT / 2f,
            0f
        );
        camera.update();

        stage = new Stage(viewport, game.getBatch());

        TextureAtlas atlas =
            assetManager.get(AssetDescriptors.ATLAS);

        Texture batSheet =
            assetManager.get(AssetDescriptors.BAT_SHEET);

        Animation<TextureRegion> batAnimation =
            BatAnimation.createBatAnimation(batSheet);

        fireballRegion =
            atlas.findRegion(RegionNames.FIREBALL);

        TextureRegion startButtonRegion =
            atlas.findRegion(START_BUTTON);

        sonarEffect = new SonarEffect();

        bat = new Bat(
            batAnimation,
            atlas.findRegion(RegionNames.BAT_PERCH)
        );

        bat.setScale(2.0f);

        float batX = WORLD_WIDTH * 0.15f;
        float batY = (WORLD_HEIGHT / 2f) - (bat.getHeight() / 2f);
        bat.setPosition(batX, batY);

        bat.setTarget(bat.getCenter());

        stage.addActor(bat);

        startButton = new StartButtonActor(startButtonRegion);

        float targetWidth = 15f;

        float aspectRatio = (float) startButtonRegion.getRegionHeight() / startButtonRegion.getRegionWidth();
        startButton.setSize(targetWidth, targetWidth * aspectRatio);

        float buttonX =
            WORLD_WIDTH / 2f - startButton.getWidth() / 2f;

        float buttonY =
            WORLD_HEIGHT / 2f - startButton.getHeight() / 2f;

        buttonX = Math.min(
            buttonX,
            WORLD_WIDTH - startButton.getWidth()
        );

        startButton.setPosition(buttonX, buttonY);
        stage.addActor(startButton);

        setupInputs();
        stage.setDebugAll(false);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        camera.update();

        stage.act(delta);

        updateAttackCooldown(delta);
        updateLocate(delta);
        handleFireballCollisions();
        bat.resetStamina();

        stage.draw();

        if (sonarEffect.isActive()) {
            sonarEffect.setOrigin(bat.getCenter());
        }

        sonarEffect.draw(camera.combined);

        if (startGameRequested) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    private void setupInputs() {
        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(stage);

        multiplexer.addProcessor(new InputAdapter() {
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

    private void activateLocate() {
        if (locateCooldownTimer > 0f || locateActive) {
            return;
        }
        game.playEchoSound();

        locateActive = true;
        locateElapsed = 0f;
        locateCurrentRange = 0f;
        locateCooldownTimer = LOCATE_COOLDOWN;

        locateOrigin.set(bat.getCenter());
        bat.getAimDirection(locateDirection);

        sonarEffect.start(
            locateOrigin,
            locateDirection,
            LOCATE_MAX_RANGE,
            LOCATE_DURATION,
            LOCATE_HALF_ANGLE
        );
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

        locateCurrentRange =
            LOCATE_MAX_RANGE * progress;

        revealStartButtonIfReached();

        if (progress >= 1f) {
            locateActive = false;
        }
    }

    private void revealStartButtonIfReached() {
        if (startButton.isRevealed()) {
            return;
        }

        float buttonCenterX =
            startButton.getX() + startButton.getWidth() / 2f;

        float buttonCenterY =
            startButton.getY() + startButton.getHeight() / 2f;

        candidateOffset.set(
            buttonCenterX - locateOrigin.x,
            buttonCenterY - locateOrigin.y
        );

        float distanceSquared = candidateOffset.len2();
        float rangeSquared =
            locateCurrentRange * locateCurrentRange;

        if (distanceSquared > rangeSquared) {
            return;
        }

        if (distanceSquared > 0.0001f) {
            candidateOffset.nor();

            float minDot =
                MathUtils.cosDeg(LOCATE_HALF_ANGLE);

            if (candidateOffset.dot(locateDirection) < minDot) {
                return;
            }
        }

        startButton.reveal();
    }

    private void tryAttack() {
        if (attackCooldownTimer > 0f) {
            return;
        }
        game.playAttackSound();

        Vector2 direction =
            bat.getAimDirection(new Vector2());

        Vector2 center = bat.getCenter();

        Fireball fireball = new Fireball(
            center.x,
            center.y,
            direction,
            fireballRegion
        );

        fireballs.add(fireball);
        stage.addActor(fireball);

        attackCooldownTimer =
            GameConfig.ATTACK_COOLDOWN;
    }

    private void updateAttackCooldown(float delta) {
        attackCooldownTimer = Math.max(
            0f,
            attackCooldownTimer - delta
        );
    }

    private void handleFireballCollisions() {
        startButtonBounds.set(
            startButton.getX(),
            startButton.getY(),
            startButton.getWidth(),
            startButton.getHeight()
        );

        for (int i = fireballs.size - 1; i >= 0; i--) {
            Fireball fireball = fireballs.get(i);

            if (fireball.isExpired()) {
                fireball.remove();
                fireballs.removeIndex(i);
                continue;
            }

            if (!startButton.isRevealed()) {
                continue;
            }

            if (fireball.getRect().overlaps(startButtonBounds)) {
                fireball.remove();
                fireballs.removeIndex(i);

                startGameRequested = true;
                return;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        camera.position.set(
            WORLD_WIDTH / 2f,
            WORLD_HEIGHT / 2f,
            0f
        );
        camera.update();
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }

        if (sonarEffect != null) {
            sonarEffect.dispose();
        }
    }

    private static class StartButtonActor extends Actor {

        private final TextureRegion region;

        private boolean revealed = false;
        private float alpha = 0f;

        StartButtonActor(TextureRegion region) {
            this.region = region;

            setSize(
                region.getRegionWidth(),
                region.getRegionHeight()
            );
        }

        void reveal() {
            revealed = true;
        }

        boolean isRevealed() {
            return revealed;
        }

        @Override
        public void act(float delta) {
            super.act(delta);

            if (revealed) {
                alpha = Math.min(1f, alpha + delta * 2.5f);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (!revealed) {
                return;
            }

            Color previousColor = new Color(batch.getColor());

            batch.setColor(
                1f,
                1f,
                1f,
                alpha * parentAlpha
            );

            batch.draw(
                region,
                getX(),
                getY(),
                getWidth(),
                getHeight()
            );

            batch.setColor(previousColor);
        }
    }
}
