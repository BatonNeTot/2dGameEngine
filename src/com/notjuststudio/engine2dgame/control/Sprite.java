package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.spr.ObjectFactory;
import org.lwjgl.opengl.GL14;

import java.io.File;
import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georgy on 03.04.2017.
 */
public class Sprite implements Draw.Drawable{

    private static Map<String, Sprite> spriteMap = new HashMap<>();

    int xOffset = 0;
    int yOffset = 0;

    boolean isFlipped = false;

    boolean isAccurateCollusionCheck = false;

    private BufferedImage image = null;
    int width = 0;
    int height = 0;


    int textureID;

    Sprite(BufferedImage image) {
        textureID = Loader.loadTexture(image, GL14.GL_MIRRORED_REPEAT);
        this.image = image;
    }

    Sprite(int textureID) {
        this.textureID = textureID;
        image = Loader.loadImageFromTexture(textureID);
    }

    Sprite(int textureID, int width, int height) {
        this.textureID = textureID;
        this.width = width;
        this.height = height;
    }

    private Sprite(String filePath) {
        com.notjuststudio.engine2dgame.xml.spr.Sprite tmp =
                Parser.loadXml(filePath, ObjectFactory.class, com.notjuststudio.engine2dgame.xml.spr.Sprite.class);

        image = ImageLoader.loadImage(new File(tmp.getSource()));
        if (image == null)
            textureID = 0;
        else
            textureID = Loader.loadTexture(image, GL14.GL_MIRRORED_REPEAT);

        isAccurateCollusionCheck = tmp.isAccurateCollusionCheck();

        xOffset = tmp.getXOffset();
        yOffset = tmp.getYOffset();
    }

    static void loadSprite(String id, String filePath) {
        spriteMap.put(id, new Sprite(filePath));
    }

    public static Sprite getSprite(String id) {
        return spriteMap.get(id);
    }

    @Override
    public void draw(Map<String, Float> parameters) {
        if (!Draw.isDraw) return;

        float x = parameters.getOrDefault("x", 0f);
        float y = parameters.getOrDefault("y", 0f);
        float xScale = parameters.getOrDefault("xScale", 1f);
        float yScale = parameters.getOrDefault("yScale", 1f);
        float alpha = parameters.getOrDefault("alpha", 1f);

        MasterRender.setShader(ShaderProgram.getSpriteShader());
        MasterRender.currentShader.loadUniform("alpha", alpha);
        MasterRender.currentShader.loadUniform("isFlipped", isFlipped);
        MasterRender.renderGUI(this.textureID,
                MasterRender.createTransformationMatrix(
                        x, y,
                        xScale, yScale,
                        0,
                        this.getWidth(), this.getHeight(),
                        this.xOffset, this.yOffset,
                        0, 0,
                        Room.getCurrentRoom().width, Room.getCurrentRoom().height)
        );
    }

    public int getWidth() {
        return image == null ? width : image.getWidth();
    }

    public int getHeight() {
        return image == null ? height : image.getHeight();
    }

    public int getData(int x, int y) {
        return image == null ? 0 : image.getRGB(x,y);
    }
}
