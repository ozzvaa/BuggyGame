package com.nevz.screens;

import static com.nevz.config.GameConfig.CHUNK_SIZE;
import static com.nevz.config.GameConfig.TREE_WIDTH;
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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nevz.BuggyGame;
import com.nevz.animation.BatAnimation;
import com.nevz.assets.AssetDescriptors;
import com.nevz.assets.RegionNames;
import com.nevz.config.GameConfig;
import com.nevz.entities.Bat;
import com.nevz.entities.Tree;

public class TreeScreen extends ScreenAdapter {
    private final BuggyGame game;
    private final Vector3 lastMousePosition = new Vector3();
    // Trees
    private final Array<TextureRegion> treeRegions = new Array<>();
    private final Array<Tree> trees = new Array<>();
    private FitViewport viewport;
    private OrthographicCamera camera;
    private boolean dragging = false;
    private boolean moveBat = false;
    private int lastMouseX, lastMouseY;
    private Stage stage;
    // Bat
    private Bat bat;


    public TreeScreen(BuggyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        AssetManager assetManager = game.getAssetManager();

        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.getBatch());

        TextureAtlas atlas =
            assetManager.get(AssetDescriptors.ATLAS);


        // Trees
        treeRegions.clear();
        treeRegions.addAll(atlas.findRegions(RegionNames.TREE));

        spawnTrees();

        // Bat
        Texture batSheet =
            assetManager.get(AssetDescriptors.BAT_SHEET);

        Animation<TextureRegion> batAnimation =
            BatAnimation.createBatAnimation(batSheet);

        bat = new Bat(batAnimation, atlas.findRegion(RegionNames.BAT_PERCH));
        stage.addActor(bat);

        stage.setDebugAll(true);
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0);

        setupInputs();

        handleTrees();

        stage.act(delta);
        stage.draw();
        drawDebug();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.update();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void drawDebug() {


        ShapeRenderer sr = game.getShapeRenderer();
        sr.setProjectionMatrix(camera.combined);

        sr.setColor(Color.GREEN);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(0, 0, WORLD_WIDTH, 0);
        sr.line(0, 0, 0, WORLD_HEIGHT);

        sr.setColor(Color.RED);
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        mouse = stage.screenToStageCoordinates(mouse);
        sr.circle(mouse.x, mouse.y, GameConfig.PERCH_RADIUS, 20);

        sr.end();
    }


    private void setupInputs() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(
            new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (button == Input.Buttons.MIDDLE) {
                        if (moveBat) {
                            Vector2 pos = stage.screenToStageCoordinates(new Vector2(screenX,screenY));
                            bat.setCenter(pos);
                            bat.setTarget(pos);
                            bat.perchAtPoint(null);
                            return true;
                        }
                        dragging = true;
                        lastMouseX = screenX;
                        lastMouseY = screenY;
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                    if (button == Input.Buttons.MIDDLE) {
                        dragging = false;
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean touchDragged(int screenX, int screenY, int pointer) {
                    if (!dragging) return false;

                    float deltaX = screenX - lastMouseX;
                    float deltaY = screenY - lastMouseY;

                    camera.position.add(
                        -deltaX * viewport.getWorldWidth() / Gdx.graphics.getWidth(),
                        deltaY * viewport.getWorldHeight() / Gdx.graphics.getHeight(),
                        0
                    );

                    lastMouseX = screenX;
                    lastMouseY = screenY;
                    camera.update();
                    return true;
                }

                @Override
                public boolean scrolled(float amountX, float amountY) {
                    camera.zoom = Math.max(0.5f, Math.min(3f,
                        camera.zoom + amountY * 0.1f));
                    camera.update();
                    return true;
                }

                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.SHIFT_LEFT) {
                        moveBat = true;
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean keyUp(int keycode) {
                    if (keycode == Input.Keys.SHIFT_LEFT) {
                        moveBat = false;
                        return true;
                    }
                    return false;
                }
            }
        );
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void spawnTrees() {
        for (int i = 0; i < treeRegions.size; i++) {
            TextureRegion tr = treeRegions.get(i);

            float width = tr.getRegionWidth();
            float height = tr.getRegionHeight();

            float scale = TREE_WIDTH / Math.min(width, height);
            width *= scale;
            height *= scale;


            Tree tree = new Tree(
                i * CHUNK_SIZE, 0, width, height, tr
            );

            tree.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    // x and y are relative to bottom left of the actor

                    if (button == Input.Buttons.LEFT) {
                        if (moveBat) {
                            Vector2 point = tree.getClosestPerchPoint(new Vector2(x, y));
                            if (point != null) {
                                Vector2 perchWorld = tree.localToStageCoordinates(point.cpy());
//                                bat.setCenter(perchWorld);
//                                bat.setTarget(perchWorld);
                                bat.perchAtPoint(perchWorld);
                            }
                            return false;
                        } else {
                            tree.debugAddPerchPoint(new Vector2(x, y));
                            return false;
                        }

                    } else if (button == Input.Buttons.RIGHT) {
                        tree.debugRemovePerchPoint(new Vector2(x, y));

                    }
                    return false;
                }
            });

            trees.add(tree);
            stage.addActor(tree);
        }
    }

    private void handleTrees() {
        for (Tree tree : trees) {
            tree.debugSetVisible();
        }
    }
}
