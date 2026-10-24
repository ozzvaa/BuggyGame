package com.nevz.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TreeData {
    public int index;
    public Array<Vector2> perchPoints = new Array<>();

    public TreeData() {}

    public TreeData(int index, Array<Vector2> perchPoints) {
        this.index = index;
        for (Vector2 point : perchPoints) {
            this.perchPoints.add(point.cpy());
        }
    }
}

