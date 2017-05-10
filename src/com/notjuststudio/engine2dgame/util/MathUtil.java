package com.notjuststudio.engine2dgame.util;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;

/**
 * Created by Georgy on 17.04.2017.
 */
public class MathUtil {

    public static Matrix3f createTranslationMatrix(Vector2f position) {
        Matrix3f result = new Matrix3f();
        result.setIdentity();

        result.m20 = position.x;
        result.m21 = position.y;

        return result;
    }

    public static Matrix3f createRotationMatrix(float angle) {
        Matrix3f result = new Matrix3f();
        result.setIdentity();

        result.m00 = (float)Math.cos(angle);
        result.m01 = (float)Math.sin(angle);
        result.m10 = -(float)Math.sin(angle);
        result.m11 = (float)Math.cos(angle);

        return result;
    }

    public static Matrix3f createScaleMatrix(Vector2f scale) {
        Matrix3f result = new Matrix3f();
        result.setIdentity();

        result.m00 = scale.x;
        result.m11 = scale.y;

        return result;
    }
}
