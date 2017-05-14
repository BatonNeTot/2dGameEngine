package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.ent.ObjectFactory;
import org.python.core.PyCode;
import org.python.core.PyException;
import org.python.core.PyNone;
import org.python.core.PyString;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georgy on 03.04.2017.
 */
public class Entity implements Comparable<Entity>{

    private static Map<String, EntityTemplate> templateMap = new HashMap<>();

    boolean isInit = false;

    Sprite sprite = null;
    private boolean visible = true;

    private float depth = 0;

    private float x = 0;
    private float y = 0;
    private float sprite_angle = 0;

    public Entity() {}

    public static Entity createEntity(PyString init, PyString step, PyString draw, PyString destroy) {
        return createEntity(init == null ? null : init.toString(),step == null ? null : step.toString(),draw == null ? null : draw.toString(),destroy == null ? null : destroy.toString());
    }

    static Entity createEntity(String init, String step, String draw, String destroy) {
        PyEngine.exec("class __tmp__(Entity, Entity.Methods):\n" +
                "  def _init_(self):\n" +
                (init == null ? "    pass" : Parser.stringParser(init, 4)) + "\n" +
                "  def _step_(self):\n" +
                (step == null ? "    pass" : Parser.stringParser(step, 4)) + "\n" +
                "  def _draw_(self):\n" +
                (draw == null ? "    pass" : Parser.stringParser(draw, 4)) + "\n" +
                "  def _destroy_(self):\n" +
                (destroy == null ? "    pass" : Parser.stringParser(destroy, 4)) + "\n" +
                "__obj__ = __tmp__()\n" +
                "del __tmp__");

        Entity result = PyEngine.get("__obj__", Entity.class);
        try {
            PyEngine.exec("del __obj__");
        } catch (PyException e) {
            PyEngine.err.println("Can't delete __obj__");
        }

        return result;
    }

    static Entity createEntity(float x, float y, String id) {
        EntityTemplate template = templateMap.get(id);

        PyEngine.exec(template.code);
        Entity result = PyEngine.get("__obj__", Entity.class);
        try {
            PyEngine.exec("del __obj__");
        } catch (PyException e) {
            PyEngine.err.println("Can't delete __obj__");
        }

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
        Room.getCurrentRoom().entities.remove(target);
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

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }
}
