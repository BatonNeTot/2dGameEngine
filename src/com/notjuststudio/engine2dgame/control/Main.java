package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.game.Game;
import com.notjuststudio.engine2dgame.xml.game.Source;
import org.lwjgl.opengl.Display;

import java.io.*;
import java.util.*;


/**
 * Created by Georgy on 31.03.2017.
 */
public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("No arguments");
                System.exit(1);
            }
            Game gameKeeper = Parser.loadXml(args[0], com.notjuststudio.engine2dgame.xml.game.ObjectFactory.class, Game.class);

            if (gameKeeper == null) {
                System.err.println("Can't load main game file");
                System.exit(2);
            }

            boolean startCheck = false;

            for (Source sprite : gameKeeper.getSprite()) {
                if (!(new File(sprite.getSource()).exists())) {
                    System.err.println("Sprite " + sprite.getId() + " in path " + sprite.getSource() + " does not exist");
                    startCheck = true;
                }
            }

            for (Source background : gameKeeper.getBackground()) {
                if (!(new File(background.getSource()).exists())) {
                    System.err.println("Background " + background.getId() + " in path " + background.getSource() + " does not exist");
                    startCheck = true;
                }
            }

            for (Source font : gameKeeper.getBackground()) {
                if (!(new File(font.getSource()).exists())) {
                    System.err.println("Font " + font.getId() + " in path " + font.getSource() + " does not exist");
                    startCheck = true;
                }
            }

            for (Source entity : gameKeeper.getEntity()) {
                if (!(new File(entity.getSource()).exists())) {
                    System.err.println("Entity " + entity.getId() + " in path " + entity.getSource() + " does not exist");
                    startCheck = true;
                }
            }

            for (Source room : gameKeeper.getRoom()) {
                if (!(new File(room.getSource()).exists())) {
                    System.err.println("Room " + room.getId() + " in path " + room.getSource() + " does not exist");
                    startCheck = true;
                }
            }

            if (startCheck) {
                System.err.println("Can't load game, check errors above");
                System.exit(3);
            }

            //init
            PyEngine.err.println("Initialization...");
            DisplayManager.init(gameKeeper.getTitle(), gameKeeper.getIcon(), DisplayManager.WINDOWED_BORDERLESS);
            Loader.init();
            MasterRender.init();
            PyEngine.init();

            //loading
            PyEngine.err.println("Loading sources...");
            for (Source sprite : gameKeeper.getSprite()) {
                Sprite.loadSprite(sprite.getId(), sprite.getSource());
            }
            for (Source background : gameKeeper.getBackground()) {
                Background.loadBackground(background.getId(), background.getSource());
            }
            for (Source font : gameKeeper.getFont()) {
                TextFont.loadFont(font.getId(), font.getSource());
            }
            for (Source entity : gameKeeper.getEntity()) {
                Entity.loadEntity(entity.getId(), entity.getSource());
            }
            for (Source room : gameKeeper.getRoom()) {
                Room.loadRoom(room.getId(), room.getSource());
            }

            Manager.background = gameKeeper.getBackgroundID();

            Room.change(gameKeeper.getRoom().get(0).getId());

            //loop
            PyEngine.err.println("Starting main loop...");
            Scanner in = new Scanner(System.in);
            while (!DisplayManager.isCloseRequested()) {

                Manager.changeRoom();

                PyEngine.execConsole();

                InputManager.update();

                if (InputManager.isKeyTouched(InputManager.KEY_F11))
                    com.notjuststudio.engine2dgame.control.Game.takeScreenshot();
                if (InputManager.isKeyTouched(InputManager.KEY_F4)) {
                    DisplayManager.setFullscreenState(DisplayManager.getFullscreenState() == 1 ? 0 : DisplayManager.getFullscreenState() + 1);
                    DisplayManager.updateDisplaySetting();
                }


                Display.setTitle(String.format("%.2f", DisplayManager.getFPS()));


                for (Entity entity : Manager.currentRoom.entities){
                    entity.step();
                }

                //prepare vao
                MasterRender.prepareRender();

                if (Manager.currentRoom.usingViews) {
                    //render to views
                    MasterRender.renderToViews();
                    //render views
                    MasterRender.renderViews();
                }
                else {
                    //render entity
                    MasterRender.renderRoom();
                }
                //render room
                MasterRender.renderPostEffect();
                //render unbind
                MasterRender.closeRender();

                //update
                DisplayManager.update();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            //clean
            PyEngine.err.println("Closing...");
            ShaderProgram.clear();
            Text.clear();
            Loader.clear();
            DisplayManager.destroy();
        }
    }
}
