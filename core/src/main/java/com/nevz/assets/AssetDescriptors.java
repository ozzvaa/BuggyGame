package com.nevz.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> ATLAS =
        new AssetDescriptor<TextureAtlas>(AssetPaths.ATLAS, TextureAtlas.class);

    public static final AssetDescriptor<Texture> BAT_SHEET =
        new AssetDescriptor<>(
            AssetPaths.BAT_SHEET,
            Texture.class
        );

    public static final AssetDescriptor<Sound> ECHO_SOUND =
        new AssetDescriptor<>("audio/echo.mp3", Sound.class);

    public static final AssetDescriptor<Sound> ATTACK_SOUND =
        new AssetDescriptor<>("audio/fireball.mp3", Sound.class);

    public static final AssetDescriptor<Music> AMBIENT_MUSIC =
        new AssetDescriptor<>("audio/forest_ambiance.mp3", Music.class);

    public static final AssetDescriptor<Sound> GAME_OVER_SOUND =
        new AssetDescriptor<>("audio/game_over.mp3", Sound.class);

    public static final AssetDescriptor<Sound> SLEEP_SOUND =
        new AssetDescriptor<>("audio/purr.mp3", Sound.class);

    private AssetDescriptors() {
    }
}
