package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.room.ObjectFactory;

import java.util.*;

/**
 * Created by Georgy on 04.04.2017.
 */
public class Room {

    private static Map<String, RoomTemplate> roomMap = new HashMap<>();

    int width;
    int height;

    List<Entity> entities;

    boolean usingViews;
    int viewWidth;
    int viewHeight;
    List<View> views;

    String background;

    static void loadRoom(String id, String filePath) {

        com.notjuststudio.engine2dgame.xml.room.Room tmp =
                Parser.loadXml(filePath, ObjectFactory.class, com.notjuststudio.engine2dgame.xml.room.Room.class);

        RoomTemplate result = new RoomTemplate();

        result.width = tmp.getWidth();
        result.height = tmp.getHeight();

        result.entities = new ArrayList<>();

        for (com.notjuststudio.engine2dgame.xml.room.Entity entity : tmp.getEntity()) {
            result.entities.add(new EntityTemplate(entity.getX(), entity.getY(), entity.getId()));
        }

        result.usingViews = tmp.isUsingViews();

        if (tmp.getViewWidth() == null || tmp.getViewHeight() == null) {
            result.viewWidth = result.width;
            result.viewHeight = result.height;
        } else {
            result.viewWidth = tmp.getViewWidth();
            result.viewHeight = tmp.getViewHeight();
        }
        result.views = new ArrayList<>();

        for (com.notjuststudio.engine2dgame.xml.room.View view : tmp.getView()) {
            result.views.add(new ViewTemplate(
                    view.getX(),
                    view.getY(),
                    view.getWidth(),
                    view.getHeight(),
                    view.getViewX(),
                    view.getViewY(),
                    view.getViewWidth(),
                    view.getViewHeight()
            ));
        }

        result.background = tmp.getBackground();

        roomMap.put(id, result);
    }

    static void setCurrentRoom(String id) {
        Room result = new Room();
        RoomTemplate template = roomMap.get(id);

        result.width = template.width;
        result.height = template.height;

        for (EntityTemplate entity : template.entities) {
            Manager.instanceCreate(entity.x, entity.y, entity.id, result);
        }

        result.usingViews = template.usingViews;

        result.viewWidth = template.viewWidth;
        result.viewHeight = template.viewHeight;

        for (ViewTemplate view : template.views) {
            result.views.add(new View(view));
        }

        result.background = template.background;

        Manager.currentRoom = result;
        Manager.currentRoom.init();
        Loader.clearFrames();
        Loader.createRoom(result);
        DisplayManager.updateDisplaySetting();
        DisplayManager.updateDisplaySize();
    }

    private Room() {
        this.views = new ArrayList<>();
        this.entities = new ArrayList<>();
    }

    void init() {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            entity.init();
        }
    }

    static class View {
        float x;
        float y;

        int width;
        int height;


        float viewX;
        float viewY;

        int viewWidth;
        int viewHeight;

        int frame;

        private View(ViewTemplate template) {
            this.x = template.x;
            this.y = template.y;
            this.width = template.width;
            this.height = template.height;
            this.viewX = template.viewX;
            this.viewY = template.viewY;
            this.viewWidth = template.viewWidth;
            this.viewHeight = template.viewHeight;
        }

    }

    private static class ViewTemplate{
        private float x;
        private float y;

        private int width;
        private int height;


        private float viewX;
        private float viewY;

        private int viewWidth;
        private int viewHeight;

        private ViewTemplate(float x, float y, int width, int height, float viewX, float viewY, int viewWidth, int viewHeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.viewX = viewX;
            this.viewY = viewY;
            this.viewWidth = viewWidth;
            this.viewHeight = viewHeight;
        }
    }

    private static class RoomTemplate {
        private int width;
        private int height;

        private List<EntityTemplate> entities;

        private boolean usingViews;
        private int viewWidth;
        private int viewHeight;
        private List<ViewTemplate> views;

        private String background;
    }

    private static class EntityTemplate {
        private String id;

        private float x;
        private float y;

        private EntityTemplate(float x, float y, String id) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

}
