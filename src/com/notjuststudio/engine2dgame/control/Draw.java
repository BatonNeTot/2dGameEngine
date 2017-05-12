package com.notjuststudio.engine2dgame.control;

import org.lwjgl.util.vector.Vector4f;

/**
 * Created by Georgy on 08.05.2017.
 */
public class Draw {

    static boolean isDraw = false;

    public static void sprite(float x, float y, String id) {
        sprite(x,y,1,id);
    }

    public static void sprite(float x, float y, float scale, String id) {
        if (!isDraw) return;
        MasterRender.setShader(ShaderProgram.getEntityShader());
        Sprite sprite = Sprite.getSprite(id);
        MasterRender.renderGUI(sprite.textureID,
                MasterRender.createTransformationMatrix(
                        x, y,
                        scale,scale,
                        0,
                        sprite.image.getWidth(), sprite.image.getHeight(),
                        sprite.xOffset, sprite.yOffset,
                        0, 0,
                        Manager.getCurrentRoom().width, Manager.getCurrentRoom().height)
        );
    }

    public static void text(float x, float y, Text text) {
        if (!isDraw) return;
        if (text.needToUpdate) text.update();
        MasterRender.setShader(ShaderProgram.getTextShader());
        MasterRender.currentShader.loadUniformLocation("color",new Vector4f(text.color.getRed(), text.color.getGreen(), text.color.getBlue(), text.color.getAlpha()));
        int xOffset = 0, yOffset = 0;
        switch (text.getAlignH()) {
            case Text.LEFT:
                xOffset = 0;
                break;
            case Text.CENTER:
                xOffset = text.getWidth() / 2;
                break;
            case Text.RIGHT:
                xOffset = text.getWidth();
                break;
        }
        switch (text.getAlignV()) {
            case Text.TOP:
                yOffset = text.getHeight();
                break;
            case Text.CENTER:
                yOffset = (text.getHeight()) / 2;
                break;
            case Text.BOTTOM:
                yOffset = (int) Math.floor((float) text.size / 8);
                break;
        }
        MasterRender.renderGUI(text.textureID,
                MasterRender.createTransformationMatrix(
                        x, y,
                        1,1,
                        0,
                        text.width, text.height,
                        xOffset, yOffset,
                        0, 0,
                        Manager.getCurrentRoom().width, Manager.getCurrentRoom().height)
        );
    }

}
