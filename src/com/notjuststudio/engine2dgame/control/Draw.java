package com.notjuststudio.engine2dgame.control;

import org.lwjgl.util.vector.Vector4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Georgy on 08.05.2017.
 */
public class Draw {

    static boolean isDraw = false;

    public static void drawable(float x, float y, Drawable drawable) {
        if (!isDraw) return;
        Map<String, Float> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        drawable.draw(parameters);
    }

    public static void sprite(float x, float y, String id) {
        sprite(x,y,1,id);
    }

    public static void sprite(float x, float y, float scale, String id) {
        sprite(x,y,scale,scale, 1,Sprite.getSprite(id));
    }

    public static void sprite(float x, float y, Sprite sprite) {
        sprite(x,y,1,1,1,sprite);
    }

    public static void sprite(float x, float y, float xScale, float yScale, float alpha, Sprite sprite) {
        if (!isDraw) return;
        Map<String, Float> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        parameters.put("xScale", xScale);
        parameters.put("yScale", yScale);
        parameters.put("alpha", alpha);
        sprite.draw(parameters);
    }

    public static void text(float x, float y, Text text) {
        if (!isDraw) return;
        Map<String, Float> parameters = new HashMap<>();
        parameters.put("x", x);
        parameters.put("y", y);
        text.draw(parameters);
    }

    static interface Drawable{

        void draw(Map<String, Float> parameters);
//        void draw(Map<String, Float> parameters, List<String> strings);

    }

}
