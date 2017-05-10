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
public class Sprite {

    private static Map<String, Sprite> spriteMap = new HashMap<>();

    int xOffset;
    int yOffset;

    boolean isAccurateCollusionCheck;

    BufferedImage image;
    int textureID;

    Sprite(BufferedImage image) {
        textureID = Loader.loadTexture(image, GL14.GL_MIRRORED_REPEAT);

        xOffset = 0;
        yOffset = 0;
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

    static Sprite getSprite(String id) {
        return spriteMap.get(id);
    }

}
