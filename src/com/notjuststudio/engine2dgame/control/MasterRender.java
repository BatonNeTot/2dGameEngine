package com.notjuststudio.engine2dgame.control;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import com.notjuststudio.engine2dgame.util.MathUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georgy on 17.04.2017.
 */
class MasterRender {

    static ShaderProgram currentShader;

    static int frameID = 0;
    static int width;
    static int height;

    static void init() {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    static void render(int target, int width, int height) {
        //prepare vao
        MasterRender.prepareRender();

        if (Room.getCurrentRoom().usingViews) {
            //render to views
            MasterRender.renderToViews();
            //render views
            MasterRender.renderViews();
        }
        else {
            //render entity
            MasterRender.renderRoom();
        }
        //render room
        MasterRender.renderPostEffect(target, width, height);
        //render unbind
        MasterRender.closeRender();
    }

    static void prepareRender() {
        Loader.bindVao(Loader.getVaoID());
    }

    static void renderToViews() {
        renderRoom(Loader.getFrameRoomID(), Room.getCurrentRoom().width, Room.getCurrentRoom().height);
    }


    static void renderViews(){
        List<Room.View> views = Room.getCurrentRoom().views;
        bindFrameBuffer(Loader.getFrameScreenID(), DisplayManager.width, DisplayManager.height);
        clearScreen(0f,0.5f,0f,1f);
        setShader(ShaderProgram.getViewShader());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.getFrameRoomTextureID());
        for (Room.View view : views) {
            currentShader.loadUniform("transformationMatrix", createTransformationMatrix(view));
            currentShader.loadUniform("point", new Vector2f(view.x / Room.getCurrentRoom().width, view.y / Room.getCurrentRoom().height));
            currentShader.loadUniform("size", new Vector2f((float)view.width / Room.getCurrentRoom().width, (float)view.height / Room.getCurrentRoom().height));
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        }
    }


    static void renderRoom() {
        renderRoom(Loader.getFrameScreenID(), DisplayManager.width, DisplayManager.height);
    }

    private static void renderRoom(int target, int width, int height) {
        bindFrameBuffer(target, width, height);
        clearScreen(0f,0f,0f,1f);
        if (Room.getCurrentRoom().background != null) {
            setShader(ShaderProgram.getBackgroundShader());
            Background back = Background.getBackground(Room.getCurrentRoom().background);
            float xScale, yScale;
            if (back.stretched) {
                xScale = 1;
                yScale = 1;
            } else {
                xScale = Room.getCurrentRoom().width / (float)back.image.getWidth();
                yScale = Room.getCurrentRoom().height / (float)back.image.getHeight();
            }
            renderBackground(back.textureID, new Vector2f(xScale, yScale));
        }
        setShader(ShaderProgram.getEntityShader());
        Room.getCurrentRoom().entities.sort(Entity::compareTo);
        Draw.isDraw = true;
        for (Entity entity : Room.getCurrentRoom().entities) {
            if (entity.isVisible() && entity.sprite != null)
                renderGUI(entity.sprite.textureID, createTransformationMatrix(entity));
            entity.draw();
            setShader(ShaderProgram.getEntityShader());
        }
        Draw.isDraw = false;
    }

    static void renderPostEffect(int target, int width, int height) {
        bindFrameBuffer(target, width, height);
        clearScreen(0f,0f,0.5f,1f);
        if (Game.background != null) {
            setShader(ShaderProgram.getBackgroundShader());
            Background back = Background.getBackground(Game.background);
            renderBackground(back.textureID, new Vector2f(Display.getWidth() / (float)back.image.getWidth(), Display.getHeight() / (float)back.image.getHeight()));
        }
        setShader(ShaderProgram.getRoomShader());
        float scale = (Display.getWidth()/(float)Display.getHeight())/(DisplayManager.width/(float)DisplayManager.height);
        currentShader.loadUniform("transformationMatrix", MathUtil.createScaleMatrix(new Vector2f(
                scale < 1 ? 1 : 1/scale,
                scale < 1 ? scale : 1
        )));
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.getFrameScreenTextureID());
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void renderRoomAnimation() {
        bindFrameBuffer(Loader.getFrameScreenID(), Display.getWidth(), Display.getHeight());
        Draw.isDraw = true;
        List<Entity> list = new ArrayList<>();
        list.add(Room.previousRoomAnimation.entity);
        list.add(Room.nextRoomAnimation.entity);
        list.sort(Entity::compareTo);
        for (Entity entity : list) {
            ((Entity.Methods)entity)._draw_();
        }
        Draw.isDraw = false;
        bindFrameBuffer(0, Display.getWidth(), Display.getHeight());
        clearScreen(0f,0f,0f,1f);
        if (Game.background != null) {
            setShader(ShaderProgram.getBackgroundShader());
            Background back = Background.getBackground(Game.background);
            renderBackground(back.textureID, new Vector2f(Display.getWidth() / (float)back.image.getWidth(), Display.getHeight() / (float)back.image.getHeight()));
        }
        setShader(ShaderProgram.getUpsideDownPartShader());
        float scale = (Display.getWidth()/(float)Display.getHeight())/(DisplayManager.width/(float)DisplayManager.height);
        currentShader.loadUniform("transformationMatrix", MathUtil.createScaleMatrix(new Vector2f(
                scale < 1 ? 1 : 1/scale,
                scale < 1 ? scale : 1
        )));
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.getFrameScreenTextureID());
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void renderText(Text text, List<Text.Char> characters, int frame, int width, int height) {
        bindFrameBuffer(frame, width, height);
        clearScreen(0f,0f,0f,0f);
        setShader(ShaderProgram.getPartShader());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.font.textureID);
        for (Text.Char character : characters) {
            currentShader.loadUniform("transformationMatrix", createTransformationMatrix(character, text, width, height));
            currentShader.loadUniform("point",
                    new Vector2f(
                            (float)text.font.getCharacter(character.id).x / text.font.source.getWidth(),
                            (float)text.font.getCharacter(character.id).y / text.font.source.getHeight()
                    ));
            currentShader.loadUniform("size",
                    new Vector2f(
                            (float)text.font.getCharacter(character.id).width / text.font.source.getWidth(),
                            (float)text.font.getCharacter(character.id).height / text.font.source.getHeight()
                    ));
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    static void clearScreen(float red, float green, float blue, float alpha) {
        GL11.glClearColor(red, green, blue, alpha);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    static void setShader(ShaderProgram shader) {
        currentShader = shader;
        currentShader.useThis();
    }

    private static void renderBackground(int ID, Vector2f scale) {
        currentShader.loadUniform("scale", scale);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void renderGUI(int ID, Matrix3f transformationMatrix) {
        currentShader.loadUniform("transformationMatrix", transformationMatrix);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    static void closeRender() {
        ShaderProgram.useNone();
        Loader.bindDefaultVAO();
    }

    static void bindFrameBuffer(int id, int width, int height) {
        Loader.bindFrameBuffer(id, width, height);
        MasterRender.frameID = id;
        MasterRender.width = width;
        MasterRender.height = height;
    }

    private static Matrix3f createTransformationMatrix(Entity entity) {
        return createTransformationMatrix(
                entity.getX(), entity.getY(),
                1,1,
                entity.getSprite_angle(),
                entity.sprite.image.getWidth(), entity.sprite.image.getHeight(),
                entity.sprite.xOffset, entity.sprite.yOffset,
                0, 0,
                Room.getCurrentRoom().width, Room.getCurrentRoom().height
        );
    }

    private static Matrix3f createTransformationMatrix(Room.View view) {
        return createTransformationMatrix(
                view.viewX, view.viewY,
                1,1,
                0,
                view.viewWidth, view.viewHeight,
                0, 0,
                0,0,
                DisplayManager.width, DisplayManager.height
        );
    }

    private static Matrix3f createTransformationMatrix(Text.Char character, Text text, int width, int height) {
        return createTransformationMatrix(
                character.x, character.y,
                1,1,
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

    static Matrix3f createTransformationMatrix(float x, float y, float xScale, float yScale,float angle, int textureWidth, int textureHeight, int xOffset, int yOffset, float roomX, float roomY, int roomWidth, int roomHeight) {

//        Vector3f scale3f = entity.getScale();
//        Vector2f scale = new Vector2f(scale3f.getX() * width/dataWidth, scale3f.getY() * height/dataHeight);

        Matrix3f result = MathUtil.createTranslationMatrix(new Vector2f((x - roomX) * 2 / roomWidth - 1, (y - roomY) * 2 / roomHeight - 1));

        Matrix3f.mul(result, MathUtil.createScaleMatrix(new Vector2f(
                textureWidth / (float) roomWidth,
                textureHeight / (float) roomHeight
        )), result);


        Matrix3f.mul(result, MathUtil.createRotationMatrix(angle), result);

        Matrix3f.mul(result, MathUtil.createScaleMatrix(new Vector2f(xScale, yScale)), result);

        Matrix3f.mul(result, MathUtil.createTranslationMatrix(new Vector2f(
                1 - xOffset * 2f / textureWidth,
                1 - yOffset * 2f / textureHeight
        )), result);

        return result;
    }
}
