package com.notjuststudio.engine2dgame.control;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import com.notjuststudio.engine2dgame.util.MathUtil;

import java.util.List;

/**
 * Created by Georgy on 17.04.2017.
 */
class MasterRender {

    static ShaderProgram currentShader;

    static void init() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    static void prepareRender() {
        Loader.bindVao(Loader.getVaoID());
    }

    static void renderToViews() {
        renderRoom(Loader.getFrameRoomID(), Manager.currentRoom.width, Manager.currentRoom.height);
    }


    static void renderViews(){
        List<Room.View> views = Manager.currentRoom.views;
        Loader.bindFrameBuffer(Loader.getFrameScreenID(), Manager.currentRoom.viewWidth, Manager.currentRoom.viewHeight);
        clearScreen(0f,0.5f,0f,1f);
        setShader(ShaderProgram.getViewShader());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.getFrameRoomTextureID());
        for (Room.View view : views) {
            currentShader.loadUniformLocation("transformationMatrix", createTransformationMatrix(view));
            currentShader.loadUniformLocation("point", new Vector2f(view.x / Manager.currentRoom.width, view.y / Manager.currentRoom.height));
            currentShader.loadUniformLocation("size", new Vector2f((float)view.width / Manager.currentRoom.width, (float)view.height / Manager.currentRoom.height));
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        }
    }


    static void renderRoom() {
        renderRoom(Loader.getFrameScreenID(), Manager.currentRoom.viewWidth, Manager.currentRoom.viewHeight);
    }

    private static void renderRoom(int target, int width, int height) {
        Loader.bindFrameBuffer(target, width, height);
        clearScreen(0.5f,0f,0f,1f);
        if (Manager.currentRoom.background != null) {
            setShader(ShaderProgram.getBackgroundShader());
            Background back = Background.getBackground(Manager.currentRoom.background);
            renderBackground(back.textureID, new Vector2f(Manager.currentRoom.width / (float)back.image.getWidth(), Manager.currentRoom.height / (float)back.image.getHeight()));
        }
        setShader(ShaderProgram.getEntityShader());
        Manager.currentRoom.entities.sort(Entity::compareTo);
        Draw.isDraw = true;
        for (Entity entity : Manager.currentRoom.entities) {
            if (!entity.isVisible()) continue;
            if (!entity.hasDraw) {
                if (entity.sprite == null) continue;
                renderGUI(Sprite.getSprite(entity.sprite).textureID, createTransformationMatrix(entity));
            } else {
                entity.draw();
                setShader(ShaderProgram.getEntityShader());
            }
        }
        Draw.isDraw = false;
    }

    static void renderPostEffect() {
        Loader.bindDefaultFrameBuffer();
        clearScreen(0f,0f,0.5f,1f);
        if (Manager.background != null) {
            setShader(ShaderProgram.getBackgroundShader());
            Background back = Background.getBackground(Manager.background);
            renderBackground(back.textureID, new Vector2f(Display.getWidth() / (float)back.image.getWidth(), Display.getHeight() / (float)back.image.getHeight()));
        }
        setShader(ShaderProgram.getRoomShader());
        float scale = (Display.getWidth()/(float)Display.getHeight())/(Manager.currentRoom.viewWidth/(float)Manager.currentRoom.viewHeight);
        currentShader.loadUniformLocation("transformationMatrix", MathUtil.createScaleMatrix(new Vector2f(
                scale < 1 ? 1 : 1/scale,
                scale < 1 ? scale : 1
        )));
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.getFrameScreenTextureID());
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void renderText(Text text, List<Text.Char> characters, int frame, int width, int height) {
        Loader.bindFrameBuffer(frame, width, height);
        clearScreen(0f,0f,0f,0f);
        setShader(ShaderProgram.getPartShader());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.font.textureID);
        for (Text.Char character : characters) {
            currentShader.loadUniformLocation("transformationMatrix", createTransformationMatrix(character, text, width, height));
            currentShader.loadUniformLocation("point",
                    new Vector2f(
                            (float)text.font.getCharacter(character.id).x / text.font.source.getWidth(),
                            (float)text.font.getCharacter(character.id).y / text.font.source.getHeight()
                    ));
            currentShader.loadUniformLocation("size",
                    new Vector2f(
                            (float)text.font.getCharacter(character.id).width / text.font.source.getWidth(),
                            (float)text.font.getCharacter(character.id).height / text.font.source.getHeight()
                    ));
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    private static void clearScreen(float red, float green, float blue, float alpha) {
        GL11.glClearColor(red, green, blue, alpha);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    static void setShader(ShaderProgram shader) {
        currentShader = shader;
        currentShader.useThis();
    }

    private static void renderBackground(int ID, Vector2f scale) {
        currentShader.loadUniformLocation("scale", scale);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void renderGUI(int ID, Matrix3f transformationMatrix) {
        currentShader.loadUniformLocation("transformationMatrix", transformationMatrix);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void closeRender() {
        ShaderProgram.useNone();
        Loader.bindDefaultVAO();
    }

    private static Matrix3f createTransformationMatrix(Entity entity) {
        Sprite sprite = Sprite.getSprite(entity.sprite);
        return createTransformationMatrix(
                entity.getX(), entity.getY(),
                entity.getSprite_angle(),
                sprite.image.getWidth(), sprite.image.getHeight(),
                sprite.xOffset, sprite.yOffset,
                0, 0,
                Manager.currentRoom.width, Manager.currentRoom.height
        );
    }

    private static Matrix3f createTransformationMatrix(Room.View view) {
        return createTransformationMatrix(
                view.viewX, view.viewY,
                0,
                view.viewWidth, view.viewHeight,
                0, 0,
                0,0,
                Manager.currentRoom.viewWidth, Manager.currentRoom.viewHeight
        );
    }

    private static Matrix3f createTransformationMatrix(Text.Char character, Text text, int width, int height) {
        return createTransformationMatrix(
                character.x, character.y,
                0,
                (int)Math.floor(text.font.getCharacter(character.id).width * text.scale),
                (int)Math.floor(text.font.getCharacter(character.id).height * text.scale),
//                0,0,
                (int)Math.floor(text.font.getCharacter(character.id).xOffset * text.scale),
                (int)Math.floor(text.font.getCharacter(character.id).yOffset * text.scale),
                0,0,
                width, height
        );
    }

    static Matrix3f createTransformationMatrix(float x, float y, float angle, int textureWidth, int textureHeight, int xOffset, int yOffset, float roomX, float roomY, int roomWidth, int roomHeight) {

//        Vector3f scale3f = entity.getScale();
//        Vector2f scale = new Vector2f(scale3f.getX() * width/dataWidth, scale3f.getY() * height/dataHeight);

        Matrix3f result = MathUtil.createTranslationMatrix(new Vector2f((x - roomX) * 2 / roomWidth - 1, (y - roomY) * 2 / roomHeight - 1));

        Matrix3f.mul(result, MathUtil.createScaleMatrix(new Vector2f(
                textureWidth / (float) roomWidth,
                textureHeight / (float) roomHeight
        )), result);


        Matrix3f.mul(result, MathUtil.createRotationMatrix(angle), result);

//        Matrix3f.mul(result, MathUtil.createScaleMatrix(scale), result);

        Matrix3f.mul(result, MathUtil.createTranslationMatrix(new Vector2f(
                1 - xOffset * 2f / textureWidth,
                1 - yOffset * 2f / textureHeight
        )), result);

        return result;
    }
}
