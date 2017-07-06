package com.notjuststudio.engine2dgame.util;

import com.notjuststudio.fpnt.FPNTContainer;
import com.notjuststudio.fpnt.FPNTDecoder;
import com.notjuststudio.fpnt.FPNTExpander;
import com.notjuststudio.fpnt.FPNTParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by George on 27.06.2017.
 */
public class Expander implements FPNTExpander {

    public static final byte
            ROOM_ENTITY = 100,
            ROOM_VIEW = 101;

    @Override
    public boolean write(OutputStream outputStream, byte b, int i, FPNTContainer fpntContainer) throws IOException {
        switch (b) {
            case ROOM_ENTITY: {
                for (Map.Entry<String, Object> entry : fpntContainer.getMaps().get(b).entrySet()) {
                    FPNTDecoder.writeKey(outputStream, entry.getKey());
                    final List<Container.Entity> list = (List<Container.Entity>) entry.getValue();
                    outputStream.write(FPNTParser.parse(list.size()));
                    for (Container.Entity entity : list) {
                        FPNTDecoder.writeKey(outputStream, entity.id);
                        outputStream.write(FPNTParser.parse(entity.x));
                        outputStream.write(FPNTParser.parse(entity.y));
                    }
                }
                return true;
            }
            case ROOM_VIEW: {
                for (Map.Entry<String, Object> entry : fpntContainer.getMaps().get(b).entrySet()) {
                    FPNTDecoder.writeKey(outputStream, entry.getKey());
                    final List<Container.View> list = (List<Container.View>) entry.getValue();
                    outputStream.write(FPNTParser.parse(list.size()));
                    for (Container.View view : list) {
                        outputStream.write(FPNTParser.parse(view.x));
                        outputStream.write(FPNTParser.parse(view.y));
                        outputStream.write(FPNTParser.parse(view.width));
                        outputStream.write(FPNTParser.parse(view.height));
                        outputStream.write(FPNTParser.parse(view.viewX));
                        outputStream.write(FPNTParser.parse(view.viewY));
                        outputStream.write(FPNTParser.parse(view.viewWidth));
                        outputStream.write(FPNTParser.parse(view.viewHeight));
                    }
                }
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public boolean read(InputStream inputStream, byte b, int length, FPNTContainer fpntContainer) throws IOException {
        switch (b) {
            case ROOM_ENTITY: {
                for (int i = 0; i < length; i++) {
                    final String key = FPNTDecoder.readKey(inputStream);
                    final byte[] count = new byte[4];
                    inputStream.read(count);
                    final int size = FPNTParser.parseInt(count);
                    final List<Container.Entity> list = new ArrayList<>(size);
                    for (int j = 0; j < size; j++) {
                        final Container.Entity entity = new Container.Entity();
                        entity.id = FPNTDecoder.readKey(inputStream);
                        inputStream.read(count);
                        entity.x = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        entity.y = FPNTParser.parseInt(count);
                        list.add(entity);
                    }
                    ((Container)fpntContainer).putEntityList(key, list);
                }
                return true;
            }
            case ROOM_VIEW: {
                for (int i = 0; i <  length; i++) {
                    final String key = FPNTDecoder.readKey(inputStream);
                    final byte[] count = new byte[4];
                    inputStream.read(count);
                    final int size = FPNTParser.parseInt(count);
                    final List<Container.View> list = new ArrayList<>(size);
                    for (int j = 0; j < size; j++) {
                        final Container.View view = new Container.View();
                        inputStream.read(count);
                        view.x = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.y = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.width = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.height = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.viewX = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.viewY = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.viewWidth = FPNTParser.parseInt(count);
                        inputStream.read(count);
                        view.viewHeight = FPNTParser.parseInt(count);
                    }
                    ((Container)fpntContainer).putViewList(key, list);
                }
                return true;
            }
            default:
                return false;
        }
    }
}
