package com.notjuststudio.engine2dgame.control;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Created by Georgy on 08.04.2017.
 */
public class Manager {

    private static String SCREENSHOT_NAME = "Screenshot";

    static String background;

    static Room currentRoom;

    static boolean isChangingRoom = false;
    static String nextRoomId;

    // INSTANCE

    static Entity instanceCreate(float x, float y, String id, Room room) {
        Entity result = Entity.createEntity(x, y, id);
        room.entities.add(result);
        return result;
    }

    public static Entity instance_create(float x, float y, String id) {
        Entity entity = instanceCreate(x,y,id,Manager.currentRoom);
        entity.init();
        return entity;
    }

    // ROOM

    static void changeRoom() {
        if (isChangingRoom) {
            isChangingRoom = false;
            Room.setCurrentRoom(nextRoomId);
        }
    }

    public static void room_goto(String id) {
        isChangingRoom = true;
        nextRoomId = id;
    }

    // GAME

    public static void game_screenshot_save() {
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

    public static void game_screenshot_save(String fileName) {
        File file = new File(fileName + ".png");
        if (file.exists())
            return;
        try {
            ImageIO.write(Loader.loadImageFromScreen(), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
