package com.notjuststudio.engine2dgame.util;

import com.notjuststudio.fpnt.FPNTContainer;
import com.sun.istack.internal.NotNull;

import java.util.List;

/**
 * Created by George on 27.06.2017.
 */
public class Container extends FPNTContainer {

    //Boolean
    public static final String
            COLLISION = "c",
            STRETCHED = "s",
            VISIBLE = "v",
            USING_VIEWS = "u";

    //Int
    public static final String
            X_OFFSET = "x",
            Y_OFFSET = "y",
            DEPTH = "d",
            WIDTH = "w",
            HEIGHT = "h";

    //String
    public static final String
            META = "m",
            SPRITE = "s",
            INIT = "i",
            STEP = "t",
            DRAW = "w",
            DESTROY = "y",
            BACKGROUND = "b",
            TITLE = "t",
            FIRST_ROOM = "r";

    //BufferedImage
    public static final String
            SOURCE = "s",
            ICON = "i";

    //Lists
    public static final String
            LIST = "l";

    public Container() {
        super(new Expander());
    }

    public Container putEntityList(@NotNull final String key, @NotNull final List<Entity> list) {
        return (Container)putValue(Expander.ROOM_ENTITY, key, list);
    }

    public Container putViewList(@NotNull final String key, @NotNull final List<View> list) {
        return (Container)putValue(Expander.ROOM_VIEW, key, list);
    }

    public List<Entity> getEntityList(@NotNull final String key) {
        return (List<Entity>)getValue(Expander.ROOM_ENTITY, key);
    }

    public List<View> getViewList(@NotNull final String key) {
        return (List<View>)getValue(Expander.ROOM_VIEW, key);
    }

    public static class Entity {

        public String id;
        public int x,y;

    }

    public static class View {

        public int
                x, y, width, height,
                viewX, viewY, viewWidth, viewHeight;

    }
}
