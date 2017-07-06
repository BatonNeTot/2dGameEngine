package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Container;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.fpnt.FPNTConstants;
import com.notjuststudio.fpnt.FPNTContainer;
import com.notjuststudio.fpnt.FPNTDecoder;
import org.lwjgl.opengl.Display;

import java.io.*;
import java.nio.file.Path;


/**
 * Created by Georgy on 31.03.2017.
 */
public class Main {

    public static void start(final Container gameKeeper, final Container[] sprites, final Container[] back, final Container[] font, final Container[] ent, final Container[] room) {
        try {
            //init
            PyEngine.err.println("Initialization...");
            DisplayManager.init(gameKeeper.getInt(Container.WIDTH), gameKeeper.getInt(Container.HEIGHT), gameKeeper.getString(Container.TITLE), gameKeeper.getBufferedImage(Container.ICON), DisplayManager.WINDOWED_BORDERLESS);
            Game.init();
            Loader.init();
            MasterRender.init();
            PyEngine.init();

            //loading
            PyEngine.err.println("Loading sources...");
            for (int i = 0; i < sprites.length; i++) {
                final Container source = sprites[i];
                Sprite.loadSprite(gameKeeper.getStringArray("Sprite")[i], source);
            }
            for (int i = 0; i < back.length; i++) {
                final Container source = back[i];
                Background.loadBackground(gameKeeper.getStringArray("Background")[i], source);
            }
            for (int i = 0; i < font.length; i++) {
                final Container source = font[i];
                TextFont.loadFont(gameKeeper.getStringArray("Font")[i], source);
            }
            for (int i = 0; i < ent.length; i++) {
                final Container source = ent[i];
                Entity.loadEntity(gameKeeper.getStringArray("Entity")[i], source);
            }
            for (int i = 0; i < room.length; i++) {
                final Container source = room[i];
                Room.loadRoom(gameKeeper.getStringArray("Room")[i], source);
            }

            Game.background = gameKeeper.getString(Container.BACKGROUND);

            Room.next(gameKeeper.getString(Container.FIRST_ROOM));

            //loop
            PyEngine.err.println("Starting main loop...");
            DisplayManager.initLoop();
            while (!DisplayManager.isCloseRequested()) {

                Room.changeRoom();

                if (DisplayManager.isCloseRequested())
                    break;

                PyEngine.execConsole();

                InputManager.update();

                if (InputManager.isKeyTouched(InputManager.KEY_F3)) {
                    Game.debug ^= true;
                }
                if (InputManager.isKeyTouched(InputManager.KEY_F4)) {
                    DisplayManager.setFullscreenState(DisplayManager.getFullscreenState() == 1 ? 0 : DisplayManager.getFullscreenState() + 1);
                    DisplayManager.updateDisplaySetting();
                }
                if (InputManager.isKeyTouched(InputManager.KEY_F11)) {
                    Game.takeScreenshot();
                }

                if (Room.isChanging) {

                    Room.time += DisplayManager.getDelta();
                    if (Room.time >= Room.endTime) {
                        Room.time = 0;
                        Room.isChanging = false;
                        continue;
                    }
                    Room.roomAnimation.step(DisplayManager.getDelta());

                    MasterRender.prepareRender();
                    MasterRender.renderRoomAnimation();
                    MasterRender.closeRender();

                } else {

                    for (Entity entity : Room.getCurrentRoom().entities) {
                        entity.step();
                    }

                    MasterRender.render(0, Display.getWidth(), Display.getHeight());
                }

                //update
                DisplayManager.update();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            //clean
            PyEngine.err.println("Closing...");
            ShaderProgram.clear();
            Text.clearUp();
            Loader.clear();
            DisplayManager.destroy();
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No arguments");
            System.exit(1);
        }

        final File gameFile = new File(args[0]).getAbsoluteFile();

        if (!FPNTDecoder.checkFile(gameFile)) {
            System.err.println("Can't load main game file");
            System.exit(2);
        }

        final Container gameKeeper = FPNTDecoder.read(gameFile, new Container());

        final Path gamePath = gameFile.getParentFile().toPath();

        boolean startCheck = false;

        for (String source : gameKeeper.getStringArray("Sprite")) {
            if (!FPNTDecoder.checkFile(gamePath.resolve("Sprite").resolve(source + FPNTConstants.EXTENSION).toFile())) {
                System.err.println("Sprite " + source + " does not exist");
                startCheck = true;
            }
        }

        for (String source : gameKeeper.getStringArray("Background")) {
            if (!FPNTDecoder.checkFile(gamePath.resolve("Background").resolve(source + FPNTConstants.EXTENSION).toFile())) {
                System.err.println("Background " + source + " does not exist");
                startCheck = true;
            }
        }

        for (String source : gameKeeper.getStringArray("Font")) {
            if (!FPNTDecoder.checkFile(gamePath.resolve("Font").resolve(source + FPNTConstants.EXTENSION).toFile())) {
                System.err.println("Font " + source + " does not exist");
                startCheck = true;
            }
        }

        for (String source : gameKeeper.getStringArray("Entity")) {
            if (!FPNTDecoder.checkFile(gamePath.resolve("Entity").resolve(source + FPNTConstants.EXTENSION).toFile())) {
                System.err.println("Entity " + source + " does not exist");
                startCheck = true;
            }
        }

        for (String source : gameKeeper.getStringArray("Room")) {
            if (!FPNTDecoder.checkFile(gamePath.resolve("Room").resolve(source + FPNTConstants.EXTENSION).toFile())) {
                System.err.println("Room " + source + " does not exist");
                startCheck = true;
            }
        }

        if (startCheck) {
            System.err.println("Can't load game, check errors above");
            System.exit(3);
        }

        String[] keys;
        keys = gameKeeper.getStringArray("Sprite");
        final Container[] sprites = new Container[keys.length];
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = FPNTDecoder.read(gamePath.resolve("Sprite").resolve(keys[i] + FPNTConstants.EXTENSION).toFile(), new Container());
        }
        keys = gameKeeper.getStringArray("Background");
        final Container[] backgrounds = new Container[keys.length];
        for (int i = 0; i < backgrounds.length; i++) {
            backgrounds[i] = FPNTDecoder.read(gamePath.resolve("Background").resolve(keys[i] + FPNTConstants.EXTENSION).toFile(), new Container());
        }
        keys = gameKeeper.getStringArray("Font");
        final Container[] fonts = new Container[keys.length];
        for (int i = 0; i < fonts.length; i++) {
            fonts[i] = FPNTDecoder.read(gamePath.resolve("Font").resolve(keys[i] + FPNTConstants.EXTENSION).toFile(), new Container());
        }
        keys = gameKeeper.getStringArray("Entity");
        final Container[] entities = new Container[keys.length];
        for (int i = 0; i < entities.length; i++) {
            entities[i] = FPNTDecoder.read(gamePath.resolve("Entity").resolve(keys[i] + FPNTConstants.EXTENSION).toFile(), new Container());
        }
        keys = gameKeeper.getStringArray("Room");
        final Container[] rooms = new Container[keys.length];
        for (int i = 0; i < rooms.length; i++) {
            rooms[i] = FPNTDecoder.read(gamePath.resolve("Room").resolve(keys[i] + FPNTConstants.EXTENSION).toFile(), new Container());
        }

        start(gameKeeper, sprites, backgrounds, fonts, entities, rooms);

        //init
        PyEngine.err.println("Initialization...");
        DisplayManager.init(gameKeeper.getInt(Container.WIDTH), gameKeeper.getInt(Container.HEIGHT), gameKeeper.getString(Container.TITLE), gameKeeper.getBufferedImage(Container.ICON), DisplayManager.WINDOWED_BORDERLESS);
        Game.init();
        Loader.init();
        MasterRender.init();
        PyEngine.init();

        //loading
//            PyEngine.err.println("Loading sources...");
//            for (String source : gameKeeper.getStringArray("Sprite")) {
//                Sprite.loadSprite(source, gamePath.resolve("Sprite").resolve(source + FPNTConstants.EXTENSION).toFile());
//            }
//            for (String source : gameKeeper.getStringArray("Background")) {
//                Background.loadBackground(source, gamePath.resolve("Background").resolve(source + FPNTConstants.EXTENSION).toFile());
//            }
//            for (String source : gameKeeper.getStringArray("Font")) {
//                TextFont.loadFont(source, gamePath.resolve("Font").resolve(source + FPNTConstants.EXTENSION).toFile());
//            }
//            for (String source : gameKeeper.getStringArray("Entity")) {
//                Entity.loadEntity(source, gamePath.resolve("Entity").resolve(source + FPNTConstants.EXTENSION).toFile());
//            }
//            for (String source : gameKeeper.getStringArray("Room")) {
//                Room.loadRoom(source, gamePath.resolve("Room").resolve(source + FPNTConstants.EXTENSION).toFile());
//            }
    }
}
