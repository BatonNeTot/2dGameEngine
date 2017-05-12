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
        Sprite sprite = Sprite.getSprite(entity.sprite);
        if (
                x >= entity.getX() - sprite.xOffset && x <= entity.getX() - sprite.xOffset + sprite.image.getWidth() &&
                        y >= entity.getY() - sprite.yOffset && y <= entity.getY() - sprite.yOffset + sprite.image.getHeight()) {
            if (sprite.isAccurateCollusionCheck) {
                x -= entity.getX() - sprite.xOffset;
                y -= entity.getY() - sprite.yOffset;
                return ((sprite.image.getRGB((int)x, (int)y)>>>24) > 2);
            } else {
                return true;
            }
        } else return false;
    }

}
