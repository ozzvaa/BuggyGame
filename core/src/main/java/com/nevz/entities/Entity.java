package com.nevz.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Entity extends Actor {
    protected final Rectangle rect = new Rectangle();

    public Entity(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
    }

    public Entity() {
        this(0, 0, 0, 0);
    }

    @Override
    protected void positionChanged() {
        rect.setPosition(getX(), getY());
    }

    @Override
    protected void sizeChanged() {
        rect.setSize(getWidth(), getHeight());
    }

    protected void setOriginCenter() {
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }

    public Rectangle getRect() {
        return rect;
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public Vector2 getCenter() {
        return new Vector2(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
    }

    public void setCenter(Vector2 pos) {
        setPosition(pos.x - getWidth() / 2f, pos.y - getHeight() / 2f);
    }

    public float getCenterX() {
        return getX() + getWidth() / 2f;
    }

    public float getCenterY() {
        return getY() + getHeight() / 2f;
    }

}
