package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Container;
import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.fpnt.FPNTDecoder;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.util.*;

/**
 * Created by Georgy on 04.04.2017.
 */
public class Room {

    static final Queue<Room> roomStack = Collections.asLifoQueue(new ArrayDeque<>());
    static String nextRoomId;

    static int changingRoomState = 0;
    static final int
            NOT_CHANGING = 0,
            CHANGING = 1,
            NEXT = 2,
            PREVIOUS = 3;

    static boolean hasAnimation = false;
    static Animation roomAnimation = null;
    static float endTime = 0;
    static float time = 0;

    static boolean isChanging = false;

    private static Map<String, RoomTemplate> roomMap = new HashMap<>();

    String name;

    int width;
    int height;

    List<Entity> entities;

    boolean usingViews;
    List<View> views;

    String background;

    static void loadRoom(String id, File filePath) {
        final Container tmp = FPNTDecoder.read(filePath, new Container());
        loadRoom(id, tmp);
    }

    static void loadRoom(String id, Container tmp) {
        RoomTemplate result = new RoomTemplate();

        result.width = tmp.getInt(Container.WIDTH);
        result.height = tmp.getInt(Container.HEIGHT);

        result.entities = new ArrayList<>();

        for (Container.Entity entity : tmp.getEntityList(Container.LIST)) {
            result.entities.add(new EntityTemplate(entity.x, entity.y, entity.id));
        }

        result.usingViews = tmp.getBoolean(Container.USING_VIEWS, false);

        result.views = new ArrayList<>();

        for (Container.View view : tmp.getViewList(Container.LIST)) {
            result.views.add(new ViewTemplate(
                    view.x,
                    view.y,
                    view.width,
                    view.height,
                    view.viewX,
                    view.viewY,
                    view.viewWidth,
                    view.viewHeight
            ));
        }

        result.background = tmp.getString(Container.BACKGROUND);

        roomMap.put(id, result);
    }

    static void setCurrentRoom(String id) {
        Room result = new Room();
        RoomTemplate template = roomMap.get(id);

        result.width = template.width;
        result.height = template.height;

        for (EntityTemplate entity : template.entities) {
            Instance.instanceCreate(entity.x, entity.y, entity.id, result);
        }

        result.usingViews = template.usingViews;

        for (ViewTemplate view : template.views) {
            result.views.add(new View(view));
        }

        result.background = template.background;

        result.name = id;

        roomStack.add(result);
        result.init();
        Loader.clearFrames();
        Loader.createRoom(result);
        DisplayManager.updateDisplaySetting();
    }

    void clear() {
        entities.clear();
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

    static void changeRoom() {
        if (changingRoomState == NOT_CHANGING)
            return;
        if (hasAnimation) {
            endTime = roomAnimation.endTime;
            isChanging = true;

            int id = Loader.createFrameBuffer();
            int texture = Loader.createTextureAttachment(Display.getWidth(), Display.getHeight());
            final boolean tmp = Game.debug;
            Game.debug = false;
            MasterRender.render(id, Display.getWidth(), Display.getHeight());
            Game.debug = tmp;
            Entity prev = roomAnimation.getEntity("prevRoom");
            prev.init();
            prev.sprite = new Sprite(texture, Display.getWidth(), Display.getHeight());
            prev.sprite.isFlipped = true;
            prev.sprite.xOffset = Room.getWidth()/2;
            prev.sprite.yOffset = Room.getHeight()/2;
        }
        switch (changingRoomState) {
            case CHANGING:
                roomStack.remove().clear();
                Room.setCurrentRoom(nextRoomId);
                break;
            case NEXT:
                Room.setCurrentRoom(nextRoomId);
                break;
            case PREVIOUS:
                roomStack.remove().clear();
                break;
        }
        if (hasAnimation && roomStack.size() != 0) {
            int id = Loader.createFrameBuffer();
            int texture = Loader.createTextureAttachment(Display.getWidth(), Display.getHeight());
            final boolean tmp = Game.debug;
            Game.debug = false;
            MasterRender.render(id, Display.getWidth(), Display.getHeight());
            Game.debug = tmp;
            Entity next = roomAnimation.getEntity("nextRoom");
            next.init();
            next.sprite = new Sprite(texture, Display.getWidth(), Display.getHeight());
            next.sprite.isFlipped = true;
            next.sprite.xOffset = Room.getWidth()/2;
            next.sprite.yOffset = Room.getHeight()/2;
            GL30.glDeleteFramebuffers(id);
        }
        if (roomStack.size() == 0) {
            DisplayManager.closeRequest();
        }
        changingRoomState = NOT_CHANGING;
        DisplayManager.resetLastFrameTime();
    }

    static Room getCurrentRoom() {
        return roomStack.peek();
    }


    public static void change(String id) {
        changingRoomState = CHANGING;
        hasAnimation = false;
        nextRoomId = id;
    }

    public static void change(String id, Animation animation) {
        changingRoomState = CHANGING;
        hasAnimation = true;
        Room.roomAnimation = animation;
        nextRoomId = id;
    }

    public static void next(String id) {
        changingRoomState = NEXT;
        hasAnimation = false;
        nextRoomId = id;
    }

    public static void next(String id, Animation animation) {
        changingRoomState = NEXT;
        hasAnimation = true;
        Room.roomAnimation = animation;
        nextRoomId = id;
    }

    public static void prev() {
        changingRoomState = PREVIOUS;
        hasAnimation = false;
    }

    public static void prev(Animation animation) {
        changingRoomState = PREVIOUS;
        hasAnimation = true;
        Room.roomAnimation = animation;
    }

    public static int getWidth() {
        return getCurrentRoom().width;
    }


    public static int getHeight() {
        return getCurrentRoom().height;
    }

}
