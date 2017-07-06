package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Container;
import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.fpnt.FPNTDecoder;
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

    private Background(Container tmp) {
        stretched = tmp.getBoolean(Container.STRETCHED);

        image = tmp.getBufferedImage(Container.SOURCE);
        if (image == null)
            textureID = 0;
        else
            textureID = Loader.loadTexture(image, GL11.GL_REPEAT);
    }

    static void loadBackground(String id, File filePath) {
        final Container tmp = FPNTDecoder.read(filePath, new Container());
        backgroundMap.put(id, new Background(tmp));
    }

    static void loadBackground(String id, Container container) {
        backgroundMap.put(id, new Background(container));
    }

    static Background getBackground(String id) {
        return backgroundMap.get(id);
    }
}
