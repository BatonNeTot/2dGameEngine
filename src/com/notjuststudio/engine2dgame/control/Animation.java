package com.notjuststudio.engine2dgame.control;

import org.python.core.PyException;

import java.util.*;

/**
 * Created by Georgy on 12.05.2017.
 */
public class Animation {

    Map<String, Set<TimeKey>> keys = new HashMap<>();
//    List<String> keysPermanent = new ArrayList<>();

    float time = 0;
    float endTime = 0;
    boolean wasPlayed = false;
    Draw.Drawable drawable = null;
    Entity entity = null;

    int interpolationType = 0;

    public static final int
            LINEAR = 0;

    public Animation(float endTime) {
        this.endTime = endTime;
    }

    public Animation(float endTime, Draw.Drawable drawable) {
        this.endTime = endTime;
        this.drawable = drawable;
    }

    public Animation(float endTime, Entity entity) {
        this.endTime = endTime;
        this.entity = entity;
    }

    public Animation step(float time) {
        this.time = Math.max(0, this.time + time);
        while (this.time >= this.endTime) {
            this.time -= this.endTime;
            wasPlayed = true;
        }
        if (this.entity != null) {
            PyEngine.put("__obj__", entity);
            for (Map.Entry<String, Float> entry : this.getTimeKey(this.time).entrySet()) {
                PyEngine.exec("__obj__." + entry.getKey() + "=" + Float.toString(entry.getValue()));
            }
            try {
                PyEngine.exec("del __obj__");
            } catch (PyException e) {
                PyEngine.err.println("Can't delete __obj__");
            }
        }
        return this;
    }

    public Animation draw() {
        if (this.drawable != null)
            draw(this.drawable);
        return this;
    }

    public Animation draw(Draw.Drawable drawable) {
        drawable.draw(this.getTimeKey(time));
        return this;
    }

    public Animation add(String key, float value) {
        return this.add(0, key, value);
    }

    public Animation add(float time, String key, float value) {
        Set<TimeKey> set = keys.get(key);
        if (set == null)
            keys.put(key, set = new TreeSet<>());

        TimeKey result = new TimeKey();
        result.time = Math.max(0, time);
        result.value = value;
        set.add(result);
        return this;
    }

    Map<String, Float> getTimeKey(float time) {
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
}
