package com.notjuststudio.engine2dgame.control;

/**
 * Created by Georgy on 10.05.2017.
 */
public class Instance {

    static Entity instanceCreate(float x, float y, String id, Room room) {
        Entity result = Entity.createEntity(x, y, id);
        room.entities.add(result);
        return result;
    }

    public static Entity create(float x, float y, String id) {
        Entity entity = instanceCreate(x,y,id, Room.getCurrentRoom());
        entity.init();
        return entity;
    }

    public static boolean isInInstance(float x, float y, Entity entity) {
        if (
                x >= entity.getX() - entity.sprite.xOffset && x <= entity.getX() - entity.sprite.xOffset + entity.sprite.getWidth() &&
                        y >= entity.getY() - entity.sprite.yOffset && y <= entity.getY() - entity.sprite.yOffset + entity.sprite.getHeight()) {
            if (entity.sprite.isAccurateCollisionCheck) {
                x -= entity.getX() - entity.sprite.xOffset;
                y -= entity.getY() - entity.sprite.yOffset;
                return ((entity.sprite.getData((int)x, (int)y)>>>24) > 2);
            } else {
                return true;
            }
        } else return false;
    }

}
