package com.notjuststudio.engine2dgame.control;

/**
 * Created by Georgy on 10.05.2017.
 */
public class Instance {

    public static Entity create(float x, float y, String id) {
        Entity entity = Manager.instanceCreate(x,y,id,Manager.getCurrentRoom());
        entity.init();
        return entity;
    }

    public static boolean isInInstance(float x, float y, Entity entity) {
        if (
                x >= entity.getX() - entity.sprite.xOffset && x <= entity.getX() - entity.sprite.xOffset + entity.sprite.image.getWidth() &&
                        y >= entity.getY() - entity.sprite.yOffset && y <= entity.getY() - entity.sprite.yOffset + entity.sprite.image.getHeight()) {
            if (entity.sprite.isAccurateCollusionCheck) {
                x -= entity.getX() - entity.sprite.xOffset;
                y -= entity.getY() - entity.sprite.yOffset;
                return ((entity.sprite.image.getRGB((int)x, (int)y)>>>24) > 2);
            } else {
                return true;
            }
        } else return false;
    }

}
