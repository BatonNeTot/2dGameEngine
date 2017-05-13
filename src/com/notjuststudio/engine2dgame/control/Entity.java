package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.ent.ObjectFactory;
import org.python.core.PyCode;
import org.python.core.PyException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georgy on 03.04.2017.
 */
public class Entity implements Comparable<Entity>{

    private static Map<String, EntityTemplate> templateMap = new HashMap<>();

    boolean isInit = false;
    boolean hasDraw = false;

    Sprite sprite = null;
    private boolean visible = true;

    private float depth = 0;

    private float x = 0;
    private float y = 0;
    private float sprite_angle = 0;

    static Entity createEntity(float x, float y, String id) {
        EntityTemplate template = templateMap.get(id);

        PyEngine.exec(template.code);
        Entity result = PyEngine.get("__obj__", Entity.class);
        try {
            PyEngine.exec("del __obj__");
        } catch (PyException e) {
            PyEngine.err.println("Can't delete __obj__");
        }

        result.hasDraw = template.hasDraw;
        result.sprite = Sprite.getSprite(template.sprite);
        result.depth = template.depth;
        result.visible = template.visible;
        result.x = x;
        result.y = y;

        return result;
    }

    static void loadEntity(String id, String filePath) {
        com.notjuststudio.engine2dgame.xml.ent.Entity tmp =
                Parser.loadXml(filePath, ObjectFactory.class, com.notjuststudio.engine2dgame.xml.ent.Entity.class);

        EntityTemplate template = new EntityTemplate();
        template.code = PyEngine.compile("class __tmp__(Entity, Entity.Methods):\n" +
                "  def _init_(self):\n" +
                (tmp.getEventInit() == null ? "    pass" : Parser.stringParser(tmp.getEventInit(), 4)) + "\n" +
                "  def _step_(self):\n" +
                (tmp.getEventStep() == null ? "    pass" : Parser.stringParser(tmp.getEventStep(), 4)) + "\n" +
                "  def _draw_(self):\n" +
                (tmp.getEventDraw() == null ? "    pass" : Parser.stringParser(tmp.getEventDraw(), 4)) + "\n" +
                "  def _destroy_(self):\n" +
                (tmp.getEventDestroy() == null ? "    pass" : Parser.stringParser(tmp.getEventDestroy(), 4)) + "\n" +
                "__obj__ = __tmp__()\n" +
                "del __tmp__");
        template.hasDraw = tmp.getEventDraw() != null;
        template.sprite = tmp.getSprite();
        template.visible = tmp.isVisible();
        template.depth = tmp.getDepth();

        templateMap.put(id, template);
    }

    void init() {
        if (!this.isInit) {
            this.isInit = true;
            ((Entity.Methods) this)._init_();
        }
    }

    void step() {
        ((Entity.Methods) this)._step_();
    }

    void draw() {
        ((Entity.Methods) this)._draw_();
    }

    void destroy() {
        Entity.destroy(this);
    }

    public static void destroy(Entity target) {
        ((Entity.Methods) target)._destroy_();
        Manager.getCurrentRoom().entities.remove(target);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getSprite_angle() {
        return sprite_angle;
    }

    public void setSprite_angle(float sprite_angle) {
        this.sprite_angle = sprite_angle;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        visible = visible;
    }

    public interface Methods{
        void _init_();
        void _step_();
        void _draw_();
        void _destroy_();
    }

    private static class EntityTemplate {
        boolean hasDraw;
        PyCode code;
        String sprite;
        boolean visible;
        float depth;
    }

    @Override
    public int compareTo(Entity o) {
        if (depth == o.depth)
            return 0;
        return depth > o.depth ? 1 : -1;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
