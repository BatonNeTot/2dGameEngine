package com.notjuststudio.engine2dgame.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georgy on 02.04.2017.
 */
public class Parser {

    public static <T> T loadXml(String filePath, Class objectFactory, Class<T> container) {
        try {
            return ((JAXBElement<T>) JAXBContext.newInstance(objectFactory).createUnmarshaller().unmarshal(new File(filePath))).getValue();
        } catch (JAXBException e) {
            System.err.println("Can't load xml");
            e.printStackTrace();
            return null;
        }

    }

    public static String stringParser(String string, int number) {
        String[] lines = string.split("\n");
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < number; i++)
            spaces.append(" ");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(spaces).append(line).append("\n");
        }
        return result.toString();
    }

    public static String stringCompiler(List<String> list, String regex) {
        StringBuilder builder = new StringBuilder(list.get(0));
        for (String part : list.subList(1, list.size()))
            builder.append(regex).append(part);
        return builder.toString();
    }

    public static String parseFile(String filePath) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            InputStream in = Class.class.getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shaderSource.toString();
    }

    public static String packageToString(Class object) {
        return "/" + (object.getPackage().toString().split("\\s+")[1].replace(".", "/")) + "/";
    }

    public static String toString(Object object) {
        return object.getClass().getSimpleName() + Integer.toHexString(object.hashCode());
    }

    public static final int
            USABLE = 0,
            BYTE = 1,
            KILO = 2,
            MEGA = 3,
            GIGO = 4;

    public static String parsMemorySize(long size) {
        return parsMemorySize(size, USABLE);
    }

    public static String parsMemorySize(long size, int flag) {
        String result = "";
        switch (flag) {
            case USABLE: {
                if (size < 1000) {
                    result = Long.toString(size) + "b";
                    break;
                }
                long tmp;
                if (size / 1024 < 1000) {
                    result = Long.toString(size / 1024) + (((tmp = (size % 1024) * 10 / 1024) == 0) ? "" : "." + Long.toString(tmp)) + "k";
                    break;
                }
                size /= 1024;
                if (size / 1024 < 1000) {
                    result = Long.toString(size / 1024) + (((tmp = (size % 1024) * 10 / 1024) == 0) ? "" : "." + Long.toString(tmp)) + "M";
                    break;
                }
                size /= 1024;
                result = Long.toString(size / 1024) + (((tmp = (size % 1024) * 10 / 1024) == 0) ? "" : "." + Long.toString(tmp)) + "G";
                break;
            }
            case BYTE: {
                result = Long.toString(size) + "b";
                break;
            }
            case KILO: {
                long tmp;
                result = Long.toString(size / 1024) + (((tmp = (size % 1024) * 10 / 1024) == 0) ? "" : "." + Long.toString(tmp)) + "k";
                break;
            }
            case MEGA: {
                long tmp;
                size /= 1024;
                result = Long.toString(size / 1024) + (((tmp = (size % 1024) * 10 / 1024) == 0) ? "" : "." + Long.toString(tmp)) + "M";
                break;
            }
            case GIGO: {
                long tmp;
                size /= 1024 * 1024;
                result = Long.toString(size / 1024) + (((tmp = (size % 1024) * 10 / 1024) == 0) ? "" : "." + Long.toString(tmp)) + "G";
                break;
            }
        }
        return result;
    }

    public static int spacesInStart(String str) {
        int sum = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ')
                sum++;
            else
                break;
        }
        return sum;
    }

}
