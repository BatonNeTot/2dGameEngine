package com.notjuststudio.engine2dgame.control;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by Georgy on 10.05.2017.
 */
public class Game {

    static String SCREENSHOT_NAME = "Screenshot";

    static String background;

    public static void takeScreenshot() {
        for (int i = 1;;i++) {
            File file = new File(SCREENSHOT_NAME + "_" + i + ".png");
            if (file.exists())
                continue;
            try {
                ImageIO.write(Loader.loadImageFromScreen(), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    public static void takeScreenshot(String fileName) {
        File file = new File(fileName + ".png");
        if (file.exists())
            return;
        try {
            ImageIO.write(Loader.loadImageFromScreen(), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeRequest() {
        DisplayManager.closeRequest();
    }

}
