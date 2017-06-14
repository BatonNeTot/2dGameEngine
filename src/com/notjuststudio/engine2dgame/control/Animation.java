package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.util.*;

/**
 * Created by Georgy on 12.05.2017.
 */
public class Animation {

    Map<String, Set<TimeKey>> keys = new HashMap<>();
//    List<String> keysPermanent = new ArrayList<>();
    Map<String, Entity> entities = new HashMap<>();

    float time = 0;
    float endTime = 0;
    boolean wasPlayed = false;
    PyObject list = null;

    int interpolationType = 0;

    public static final int
            LINEAR = 0;

    {
        PyEngine.put(Parser.toString(this), this);
        PyEngine.exec(Parser.toString(this) + ".list=List()");
    }

    public Animation(float endTime) {
        this.endTime = endTime;
    }

    public Animation(float endTime, Entity entity) {
        this.endTime = endTime;
        this.addEntity(entity);
    }

    public Animation step(float time) {
        this.time = Math.max(0, this.time + time);
        while (this.time >= this.endTime) {
            this.time -= this.endTime;
            wasPlayed = true;
        }
        return this.update();
    }

//    public Animation draw() {
//        if (this.drawable != null)
//            draw(this.drawable);
//        return this;
//    }
//
//    public Animation draw(Draw.Drawable drawable) {
//        drawable.draw(this.getTimeKey(time));
//        return this;
//    }

    public Animation addEntity(Entity entity) {
        return addEntity("default", entity);
    }

    public Animation addEntity(String key, Entity entity) {
        entities.put(key, entity);
        return this;
    }

    public Entity getEntity() {
        return this.getEntity("default");
    }

    public Entity getEntity(String key) {
        return entities.get(key);
    }

    public Animation addParameter(String key, float value) {
        return this.addParameter(0, key, value);
    }

    public Animation addParameter(float time, String key, float value) {
        Set<TimeKey> set = keys.get(key);
        if (set == null)
            keys.put(key, set = new TreeSet<>());

        TimeKey result = new TimeKey();
        result.time = Math.max(0, time);
        result.value = value;
        set.add(result);
        return this.update();
    }

    public Animation update() {
        if (!this.entities.isEmpty()) {
            for (Map.Entry<String, Entity> entry : entities.entrySet()) {
                PyEngine.put("__tmp__", entry.getValue());
                PyEngine.exec(Parser.toString(this) + ".list." + entry.getKey() + "=__tmp__");
            }

            for (Map.Entry<String, Float> entry : this.getTimeKey().entrySet()) {
                String key = entry.getKey();
                boolean flag = false;
                for (Map.Entry<String, Entity> entity : entities.entrySet()) {
                    if (key.startsWith(entity.getKey())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag)
                    key = "default." + key;
                PyEngine.exec(Parser.toString(this) + ".list." + key + "=" + entry.getValue());
            }
        }
        return this;
    }

    public Animation resetTime() {
        this.time = 0;
        return this;
    }

    public Map<String, Float> getTimeKey() {
        return this.getTimeKey(this.time);
    }

    public Map<String, Float> getTimeKey(float time) {
        Map<String, Float> result = new HashMap<>();
        keysLoop:
        for (Map.Entry<String, Set<TimeKey>> entry : keys.entrySet()) {
            Iterator<TimeKey> iterator = entry.getValue().iterator();
            TimeKey first =  iterator.next();
            if (first.time >= time) {
                result.put(entry.getKey(), first.value);
                continue;
            }
            while (iterator.hasNext()) {
                TimeKey tmp = iterator.next();
                if (tmp.time == time) {
                    result.put(entry.getKey(), tmp.value);
                    continue keysLoop;
                }
                if (tmp.time > time) {
                    switch (interpolationType) {
                        case LINEAR:
                            result.put(entry.getKey(), first.value + (tmp.value - first.value) * (time - first.time)/(tmp.time - first.time));
                            continue keysLoop;
                    }
                } else {
                    first = tmp;
                }
            }
            result.put(entry.getKey(), first.value);
        }
        return result;
    }

    class TimeKey implements Comparable<TimeKey>{

        float time;
        float value;

        @Override
        public int compareTo(TimeKey o) {
            if (time == o.time)
                return 0;
            return time > o.time ? 1 : -1;
        }
    }

    public float getTime() {
        return time;
    }

    public boolean isWasPlayed() {
        return wasPlayed;
    }

    public PyObject getList() {
        return list;
    }

    public void setList(PyObject list) {
        if (this.list == null)
            this.list = list;
    }
}
