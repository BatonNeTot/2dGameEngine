package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.Arrays;

import java.nio.ByteBuffer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * Created by Georgy on 31.03.2017.
 */
public class DisplayManager {

    private static int countOfDelta = 0;
    private static int maxCountOfDelta = 100;
    private static List<Float> listOfDelta = new ArrayList<>(maxCountOfDelta);

    static int width;
    static int height;

    private static long lastFrameTime;
    static float delta;

    private static boolean closeRequest = false;
    private static final ContextAttribs attribs;
    private static final DisplayMode[] modes;
    private static DisplayMode fullscreenMode;
    private static PixelFormat format;

    public static final int
            WINDOWED = 0,
            WINDOWED_BORDERLESS = 1,
            FULLSCREEN = 2;
    private static Setting currentSetting = new Setting();
    private static Setting newSetting = new Setting();

    static {
        attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        DisplayMode[] tmp;
        try {
            tmp = Display.getAvailableDisplayModes();
            Arrays.sort(tmp, new DisplayModeComparator());
        } catch (LWJGLException e) {
            e.printStackTrace();
            tmp = new DisplayMode[1];
            tmp[0] = Display.getDesktopDisplayMode();
        }
        modes = tmp;
        fullscreenMode = Display.getDesktopDisplayMode();
        format = new PixelFormat().withSamples(4);
    }

    static void init(int width, int height, String title, String icon, int state) {

        DisplayManager.width = width;
        DisplayManager.height = height;

        ByteBuffer[] icons = new ByteBuffer[2];
        if (icon != null) {
            BufferedImage image = ImageLoader.loadImage(new File(icon));
            if (image != null) {
                icons[0] = ImageLoader.imageToBuffer(ImageLoader.resize(image, 16, 16));
                icons[1] = ImageLoader.imageToBuffer(ImageLoader.resize(image, 32, 32));
            } else {
                icon = null;
            }
        }
        try {
            if (icon != null) {
                Display.setIcon(icons);
            }
            Display.setTitle(title);
//            Display.setVSyncEnabled(true);
            System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(true));
            Display.setDisplayMode(new DisplayMode(0,0));
            Display.create(format, attribs);
            System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(false));

            GL11.glEnable(GL13.GL_MULTISAMPLE);
            setFullscreenState(state);
        } catch (LWJGLException e) {
            //handle exception
            e.printStackTrace();
        }

        lastFrameTime = getCurrentTime();
    }

    static void update() {
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = getCurrentTime();

        if (countOfDelta < maxCountOfDelta) {
            countOfDelta++;
        } else {
            listOfDelta.remove(0);
        }
        listOfDelta.add(delta);
    }

    static void destroy() {
        Display.destroy();
    }

    public static boolean isCloseRequested() {
        return Display.isCloseRequested() || closeRequest;
    }

    static void closeRequest() {
        closeRequest = true;
    }

    static void initSize() {
        if (Display.getDisplayMode().getWidth() == 0 || Display.getDisplayMode().getHeight() == 0){
            updateDisplaySize();
        }
    }

    static void setSize(int width, int height) {
        try {
            GL11.glViewport(0, 0, width, height);
            Display.setDisplayMode(new DisplayMode(width, height));
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public static float getFPS() {
        if (listOfDelta.isEmpty()) return 0;
        return countOfDelta / ((float) listOfDelta.stream().mapToDouble(Float::floatValue).sum());
    }

    public static long getCurrentTime() {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }

    public static int getFullscreenState() {
        return currentSetting.fullscreenState;
    }

    public static void setFullscreenState(int state) {
        if (state >= 0 && state <= 2)
            newSetting.fullscreenState = state;
    }

    public static void updateDisplaySetting() {
        if (currentSetting.fullscreenState != newSetting.fullscreenState) {
            try {
                switch (newSetting.fullscreenState) {
                    case WINDOWED: {
                        System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(false));
                        updateDisplaySize();
                        Display.setFullscreen(false);
                        break;
                    }
                    case WINDOWED_BORDERLESS: {
                        System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(true));
                        Display.setDisplayMode(fullscreenMode);
                        GL11.glViewport(0, 0, fullscreenMode.getWidth(), fullscreenMode.getHeight());
                        Display.setFullscreen(false);
                        break;
                    }
                    case FULLSCREEN: {
                        System.setProperty("org.lwjgl.opengl.Window.undecorated", Boolean.toString(false));
                        Display.setDisplayMode(fullscreenMode);
                        GL11.glViewport(0, 0, fullscreenMode.getWidth(), fullscreenMode.getHeight());
                        Display.setFullscreen(true);
                        break;
                    }
                }
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
        currentSetting = newSetting.clone();
    }

    static void updateDisplaySize() {
        DisplayManager.setSize(DisplayManager.width, DisplayManager.height);
    }

    private static class Setting implements Cloneable {
        private int fullscreenState = WINDOWED;

        @Override
        protected Setting clone() {
            try {
                return (Setting)super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return new Setting();
            }
        }
    }
}
