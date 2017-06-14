package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;

import java.awt.image.BufferedImage;

import java.nio.IntBuffer;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by Georgy on 31.03.2017.
 */
public class Loader {

    private static int vaoID = 0;
    private static int vboID = 0;

    private static int frameScreenID = 0;
    private static int frameScreenTextureID = 0;
    private static int frameRoomID = 0;
    private static int frameRoomTextureID = 0;

    private static Set<Integer> textureList = new HashSet<>();

    private static boolean anisotropicFilter;

    static {
        if (!(anisotropicFilter = GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic))
            System.err.println("Anisotropic filter is not supported!");
    }

    static void init() {

        vaoID = createVAO();
        float[] data = {-1, 1, -1, -1, 1, 1, 1, -1};
        vboID = storeDataInAttributeList(0, 2, storeDataInFloatBuffer(data));
        GL20.glEnableVertexAttribArray(0);
        bindNoneVAO();

        frameScreenID = createFrameBuffer();
        frameScreenTextureID = createTextureAttachment(DisplayManager.width, DisplayManager.height);

        TextFont.loadFont(
                "default",
                ShaderProgram.DEFAULT_PATH + "debug.png",
                ShaderProgram.DEFAULT_PATH + "debug.fnt");
    }

    static void clear() {
        GL30.glDeleteVertexArrays(vaoID);
        GL15.glDeleteBuffers(vboID);
        textureList.forEach(GL11::glDeleteTextures);
        clearFrames();
        GL30.glDeleteFramebuffers(frameScreenID);
        GL11.glDeleteTextures(frameScreenTextureID);
    }

    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    static int[] createTextVAO(float[] positions, float[] uvs) {
        int[] result = new int[4];
        result[0] = createVAO();
        int[] indices = new int[positions.length / 4 * 3];
        for (int i = 0; i < positions.length / 8; i++) {
            indices[i * 6] = i * 4;
            indices[i * 6 + 1] = i * 4 + 2;
            indices[i * 6 + 2] = i * 4 + 1;
            indices[i * 6 + 3] = i * 4 + 1;
            indices[i * 6 + 4] = i * 4 + 2;
            indices[i * 6 + 5] = i * 4 + 3;
        }
        result[1] = storeDataInIndicesBuffer(storeDataInIntBufferBuffer(indices));
        result[2] = storeDataInAttributeList(0, 2, storeDataInFloatBuffer(positions));
        result[3] = storeDataInAttributeList(1, 2, storeDataInFloatBuffer(uvs));
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        bindNoneVAO();
        return result;
    }

    static void bindVao(int vaoID) {
        GL30.glBindVertexArray(vaoID);
    }

    static void bindNoneVAO() {
        GL30.glBindVertexArray(0);
    }

    static void bindFrameBuffer(int frame, int width, int height) {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frame);
        GL11.glViewport(0, 0, width, height);
    }

    static void bindDefaultFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }

    private static int storeDataInAttributeList(int attributeNumber, int size, FloatBuffer data) {
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vboID;
    }

    private static int storeDataInIndicesBuffer(IntBuffer indices) {
        int vboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        return vboID;
    }

    static FloatBuffer storeDataInFloatBuffer(float[] data) {
        return (FloatBuffer) BufferUtils.createFloatBuffer(data.length).put(data).flip();
    }

    public static IntBuffer storeDataInIntBufferBuffer(int[] data) {
        return (IntBuffer) BufferUtils.createIntBuffer(data.length).put(data).flip();
    }

    static int loadTexture(BufferedImage image, int clamp) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(texID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, ImageLoader.imageToBuffer(image));

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
        if (anisotropicFilter) {
            float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, clamp);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, clamp);

        textureList.add(texID);
        return texID;
    }

    static BufferedImage loadImageFromScreen() {
        GL11.glReadBuffer(GL11.GL_FRONT);

        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();

        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder());

        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );

        BufferedImage bImageFromConvert = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                bImageFromConvert.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return bImageFromConvert;
    }

    static BufferedImage loadImageFromTexture(int textureID) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder());

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        BufferedImage bImageFromConvert = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;
                bImageFromConvert.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }

        return bImageFromConvert;
    }

    static void createRoom(Room room) {
        frameRoomID = createFrameBuffer();
        frameRoomTextureID = createTextureAttachment(room.width, room.height);
        bindDefaultFrameBuffer();
    }

    static int createFrameBuffer() {
        int frameBuffer = GL30.glGenFramebuffers();
        //generate name for frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        //create the framebuffer
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        //indicate that we will always renderToViews to color attachment 0
        return frameBuffer;
    }

    static int createTextureAttachment(int width, int height) {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height,0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
        if (anisotropicFilter) {
            float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
                texture, 0);
        return texture;
    }

    static int getVaoID() {
        return vaoID;
    }

    static void clearFrames(){
        GL30.glDeleteFramebuffers(frameRoomID);
        GL11.glDeleteTextures(frameRoomTextureID);
        frameRoomID = 0;
        frameRoomTextureID = 0;
    }

    static int getFrameScreenID() {
        return frameScreenID;
    }

    static int getFrameScreenTextureID() {
        return frameScreenTextureID;
    }

    static int getFrameRoomID() {
        return frameRoomID;
    }

    static int getFrameRoomTextureID() {
        return frameRoomTextureID;
    }
}
