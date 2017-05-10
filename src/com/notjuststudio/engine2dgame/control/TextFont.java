package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.font.ObjectFactory;
import org.lwjgl.opengl.GL14;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Georgy on 05.05.2017.
 */
class TextFont {

    private static Map<String, TextFont> fontMap = new HashMap<>();

    private Map<Character, Char> charMap = new HashMap<>();

    BufferedImage source;
    int textureID;

    int size;

    private TextFont(String filePath) {
        com.notjuststudio.engine2dgame.xml.font.Font tmp =
                Parser.loadXml(filePath, ObjectFactory.class, com.notjuststudio.engine2dgame.xml.font.Font.class);

        source = ImageLoader.loadImage(new File(tmp.getSource()));
        textureID = Loader.loadTexture(source, GL14.GL_MIRRORED_REPEAT);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(tmp.getMeta())));

            String line = reader.readLine();
            String[] fields = line.split(" ");
            String field = "";
            for (int i = 0; !field.startsWith("padding"); i++)
                field = fields[i];

            for (int i = 0; !field.startsWith("size"); i++)
                field = fields[i];

            size = Integer.parseInt(field.split("=")[1]);

            reader.readLine();
            reader.readLine();
            int count = Integer.parseInt(reader.readLine().split(" ")[1].split("=")[1]);

            for (int i = 0; i < count; i++) {
                fields = reader.readLine().split(" ");
                ArrayList<String> list = new ArrayList<>(Arrays.asList(fields));
                for (int j = list.size() - 1; j >= 0; j--)
                    if (list.get(j).isEmpty())
                        list.remove(j);
                charMap.put((char)parse(list.get(1)), new Char(
                        parse(list.get(2)),
                        parse(list.get(3)),
                        parse(list.get(4)),
                        parse(list.get(5)),
                        parse(list.get(6)),
                        parse(list.get(5)) - ((int)(size * 1) - parse(list.get(7))),
                        parse(list.get(8))
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't read meta file " + tmp.getMeta() + " from " + filePath);
        }

    }

    static void loadFont(String id, String filePath) {
        fontMap.put(id, new TextFont(filePath));
    }

    private int parse(String field) {
        return Integer.parseInt(field.split("=")[1]);
    }

    class Char {
        int x;
        int y;

        int width;
        int height;

        int xOffset;
        int yOffset;

        int step;

        public Char(int x, int y, int width, int height, int xOffset, int yOffset, int step) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.step = step;
        }
    }

    static TextFont getFont(String id) {
        return fontMap.get(id);
    }

    static String getName(TextFont font) {
        for (Map.Entry<String, TextFont> entry : fontMap.entrySet())
            if (entry.getValue().equals(font))
                return entry.getKey();
        return "None";
    }

    Char getCharacter(char character) {
        Char result;
        if ((result = charMap.get(character)) == null)
            result = charMap.get((char)0);
        return result;
    }

}
