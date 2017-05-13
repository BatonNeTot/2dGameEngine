package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Parser;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by George on 09.05.2017.
 */
public class Text implements Draw.Drawable{

    private static List<Text> texts = new ArrayList<>();

    boolean needToUpdate = true;

    Color color = Color.BLACK;

    private int alignV = 0;
    private int alignH = 0;
    private int letterSpacing = 0;

    private String text;
    int size = 64;
    float scale;
    int width;
    int height;

    TextFont font;
    int textureID = 0;

    public static final int
            LEFT = 0,
            BOTTOM = 0,
            CENTER = 1,
            RIGHT = 2,
            TOP = 2;

    {
        texts.add(this);
    }

    public Text(String text, String id) {
        this.font = TextFont.getFont(id);
        this.scale = (float)this.size/this.font.size;
        this.text = text;
        setWidth();
        setHeight();
    }

    public Text(String text, String id, int width) {
        this.font = TextFont.getFont(id);
        this.scale = (float)this.size/this.font.size;
        this.text = text;
        this.width = width;
        setHeight();
    }

    public int getWidth() {
        return width;
    }

    public int getLineWidth(String line) {
        int sum = 0;
        for (char character : line.toCharArray())
            sum += Math.floor(font.getCharacter(character).step * scale) + letterSpacing;
        return sum;
    }

//    public Text setWidth(int width) {
//        this.width = width;
//        this.needToUpdate = true;
//        return this;
//    }

    private void setWidth() {
        int sum, max = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            sum = 0;
            for (char character : line.toCharArray())
                sum += Math.floor(font.getCharacter(character).step * scale) + letterSpacing;
            max = sum > max ? sum : max;
        }
        width = max;
    }

    public int getHeight() {
        return height;
    }

//    public Text setHeight(int height) {
//        this.height = height;
//        this.needToUpdate = true;
//        return this;
//    }

    private void setHeight() {
        int sum = 0, tmp, size = this.size * 9 / 8;
        String[] lines = text.split("\n");
        for (String line : lines) {
            tmp = 0;
            String[] words = line.split(" ");
            for (String word : words) {
                int length = 0;
                for (char character : word.toCharArray())
                    length += Math.floor(font.getCharacter(character).step * scale);
                if ((tmp += length) > width) {
                    sum += size;
                    tmp = length;
                }
            }
            sum += size;
        }
        sum += (float) size / 9;
        height = sum;
    }

    private String[] format() {
        List<String> lines = new ArrayList<>(Arrays.asList(this.text.split("\n")));
        for (int i = 0; i < lines.size(); i++) {
            if (this.getLineWidth(lines.get(i)) <= this.width) continue;
            String[] words = lines.get(i).split(" ");
            lines.remove(i);
            StringBuilder line = new StringBuilder(words[0]);
            for (String word : Arrays.asList(words).subList(1, words.length)) {
                if (this.getLineWidth(line.toString() + this.getLineWidth(" " + word)) > this.width) {
                    lines.add(i, line.toString());
                    i++;
                    line = new StringBuilder(word);
                } else {
                    line.append(" ").append(word);
                }
            }
            lines.add(i, line.toString());
        }
        return lines.toArray(new String[lines.size()]);
    }

    void update() {
        int target = MasterRender.frameID;
        int width = MasterRender.width;
        int height = MasterRender.height;

        GL11.glDeleteTextures(textureID);
        int id = Loader.createFrameBuffer();
        setWidth();
        setHeight();
        textureID = Loader.createTextureAttachment(this.width, this.height);
//        MasterRender.prepareRender();
        List<Char> characters = new ArrayList<>();
        int xCursor = 0;
        int yCursor = this.height - (int) Math.floor((float) size  * 9 / 8);

        String[] lines = format();
        for (String line : lines) {
            switch (this.alignH) {
                case LEFT:
                    xCursor = 0;
                    break;
                case CENTER:
                    xCursor = (this.width - this.getLineWidth(line)) / 2;
                    break;
                case RIGHT:
                    xCursor = this.width - this.getLineWidth(line);
                    break;
            }
            for (char symbol : line.toCharArray()) {
                Char character = new Char();
                character.id = symbol;
                character.x = xCursor;
                character.y = yCursor;
                characters.add(character);
                xCursor += Math.floor(font.getCharacter(symbol).step * scale) + letterSpacing;
            }
            yCursor -= this.size * 9 / 8;
        }

        MasterRender.renderText(this, characters, id, this.width, this.height);

        MasterRender.bindFrameBuffer(target, width, height);
//        MasterRender.closeRender();
        GL30.glDeleteFramebuffers(id);

        needToUpdate = false;
    }

    @Override
    public void draw(Map<String, Float> parameters) {
        if (!Draw.isDraw) return;
        float x = parameters.getOrDefault("x", 0f);
        float y = parameters.getOrDefault("y", 0f);
        this.setSize((int)Math.floor(parameters.getOrDefault("size", (float)this.size)));

        if (this.needToUpdate) this.update();
        MasterRender.setShader(ShaderProgram.getTextShader());
        MasterRender.currentShader.loadUniformLocation("color",new Vector4f(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha()));
        int xOffset = 0, yOffset = 0;
        switch (this.getAlignH()) {
            case Text.LEFT:
                xOffset = 0;
                break;
            case Text.CENTER:
                xOffset = this.getWidth() / 2;
                break;
            case Text.RIGHT:
                xOffset = this.getWidth();
                break;
        }
        switch (this.getAlignV()) {
            case Text.TOP:
                yOffset = this.getHeight();
                break;
            case Text.CENTER:
                yOffset = (this.getHeight()) / 2;
                break;
            case Text.BOTTOM:
                yOffset = (int) Math.floor((float) this.size / 8);
                break;
        }
        MasterRender.renderGUI(this.textureID,
                MasterRender.createTransformationMatrix(
                        x, y,
                        1,1,
                        0,
                        this.width, this.height,
                        xOffset, yOffset,
                        0, 0,
                        Manager.getCurrentRoom().width, Manager.getCurrentRoom().height)
        );
    }

    class Char {
        char id;
        int x, y;
    }

    static void clear() {
        for (Text text : texts){
            GL11.glDeleteTextures(text.textureID);
        }
    }

    public Color getColor() {
        return color;
    }

    public Text setColor(Color color) {
        this.color = color;
        return this;
    }

    public int getAlignV() {
        return alignV;
    }

    public Text setAlignV(int alignV) {
        this.alignV = alignV;
        this.needToUpdate = true;
        return this;
    }

    public int getAlignH() {
        return alignH;
    }

    public Text setAlignH(int alignH) {
        this.alignH = alignH;
        this.needToUpdate = true;
        return this;
    }

    public int getLetterSpacing() {
        return letterSpacing;
    }

    public Text setLetterSpacing(int letterSpacing) {
        this.letterSpacing = letterSpacing;
        this.needToUpdate = true;
        return this;
    }

    public String getText() {
        return text;
    }

    public Text setText(String text) {
        this.text = text;
        this.needToUpdate = true;
        return this;
    }

    public Text addText(String text) {
        this.text += text;
        this.needToUpdate = true;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Text setSize(int size) {
        this.size = size;
        this.scale = (float)this.size/this.font.size;
        this.needToUpdate = true;
        return this;
    }

    public TextFont getFont() {
        return font;
    }

    public Text setFont(String id) {
        this.font = TextFont.getFont(id);
        this.needToUpdate = true;
        return this;
    }
}
