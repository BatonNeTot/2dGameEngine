package com.notjuststudio.engine2dgame.control;

import com.notjuststudio.engine2dgame.util.Container;
import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.fpnt.FPNTDecoder;
import org.lwjgl.opengl.GL14;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Created by Georgy on 05.05.2017.
 */
class TextFont {

    private static Map<String, TextFont> fontMap = new HashMap<>();

    private Map<Character, Char> charMap = new HashMap<>();

    BufferedImage source;
    int textureID;

    int size;

    TextFont(BufferedImage source, String metaSource) {
        this.source = source;
        textureID = Loader.loadTexture(source, GL14.GL_MIRRORED_REPEAT);

        final Scanner reader = new Scanner(metaSource);

        String line = reader.nextLine();
        String[] fields = line.split(" ");
        String field = "";
        for (int i = 0; !field.startsWith("padding"); i++)
            field = fields[i];

        for (int i = 0; !field.startsWith("size"); i++)
            field = fields[i];

        size = Integer.parseInt(field.split("=")[1]);

        reader.nextLine();
        reader.nextLine();
        int count = Integer.parseInt(reader.nextLine().split(" ")[1].split("=")[1]);

        for (int i = 0; i < count; i++) {
            fields = reader.nextLine().split(" ");
            ArrayList<String> list = new ArrayList<>(Arrays.asList(fields));
            for (int j = list.size() - 1; j >= 0; j--)
                if (list.get(j).isEmpty())
                    list.remove(j);
            charMap.put((char) parse(list.get(1)), new Char(
                    parse(list.get(2)),
                    parse(list.get(3)),
                    parse(list.get(4)),
                    parse(list.get(5)),
                    parse(list.get(6)),
                    parse(list.get(5)) - ((int) (size * 1) - parse(list.get(7))),
                    parse(list.get(8))
            ));
        }
        reader.close();
    }

    TextFont(String sourcePath, String metaPath) {
        source = ImageLoader.loadImage(new File(sourcePath));
        textureID = Loader.loadTexture(source, GL14.GL_MIRRORED_REPEAT);

        BufferedReader reader = null;
        try {
            try {
                String meta = metaPath.startsWith("/") ? metaPath : "/" + metaPath;
                InputStream in = Class.class.getResourceAsStream(meta);
                reader = new BufferedReader(new InputStreamReader(in));
            } catch (NullPointerException e) {
                reader = new BufferedReader(new FileReader(new File(metaPath)));
            }

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
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadFont(String id, File filePath) {
        final Container tmp = FPNTDecoder.read(filePath, new Container());
        loadFont(id, tmp.getBufferedImage(Container.SOURCE), tmp.getString(Container.META));
    }

    static void loadFont(String id, Container tmp) {
        loadFont(id, tmp.getBufferedImage(Container.SOURCE), tmp.getString(Container.META));
    }

    static void loadFont(String id, BufferedImage source, String metaSource) {
        fontMap.put(id, new TextFont(source, metaSource));
    }

    static void loadFont(String id, String sourcePath, String metaPath) {
        fontMap.put(id, new TextFont(sourcePath, metaPath));
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
        if (fontMap.containsKey(id))
            return fontMap.get(id);
        else
            return fontMap.get("default");
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
