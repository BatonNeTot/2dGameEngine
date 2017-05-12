package com.notjuststudio.engine2dgame.control;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

/**
 * Created by Georgy on 08.04.2017.
 */
public class Manager {

    static String SCREENSHOT_NAME = "Screenshot";

    static String background;

    static final Queue<Room> roomStack = Collections.asLifoQueue(new ArrayDeque<>());

    static int changingRoomState = 0;
    static final int
            NOT_CHANGING = 0,
            CHANGING = 1,
            NEXT = 2,
            PREVIOUS = 3;
    static String nextRoomId;

    // INSTANCE

    static Entity instanceCreate(float x, float y, String id, Room room) {
        Entity result = Entity.createEntity(x, y, id);
        room.entities.add(result);
        return result;
    }

    // ROOM

    static void changeRoom() {
        switch (changingRoomState) {
            case NOT_CHANGING:
                return;
            case CHANGING:
                roomStack.remove();
                Room.setCurrentRoom(nextRoomId);
                break;
            case NEXT:
                Room.setCurrentRoom(nextRoomId);
                break;
            case PREVIOUS:
                roomStack.remove();
                if (roomStack.peek() == null)
                    DisplayManager.closeRequest();
                break;
        }
        changingRoomState = NOT_CHANGING;
    }

    static Room getCurrentRoom() {
        return roomStack.peek();
    }

    // GAME


}
