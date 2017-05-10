package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.back.ObjectFactory;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georgy on 03.05.2017.
 */
public class Background {

    private static Map<String, Background> backgroundMap = new HashMap<>();

    boolean stretched;

    BufferedImage image;
    int textureID;

    private Background(String filePath) {
        com.notjuststudio.engine2dgame.xml.back.Background tmp =
                Parser.loadXml(filePath, ObjectFactory.class, com.notjuststudio.engine2dgame.xml.back.Background.class);

        stretched = tmp.isStretched();

        image = ImageLoader.loadImage(new File(tmp.getSource()));
        if (image == null)
            textureID = 0;
        else
            textureID = Loader.loadTexture(image, GL11.GL_REPEAT);
    }

    static void loadBackground(String id, String filePath) {
        backgroundMap.put(id, new Background(filePath));
    }

    static Background getBackground(String id) {
        return backgroundMap.get(id);
    }
}
