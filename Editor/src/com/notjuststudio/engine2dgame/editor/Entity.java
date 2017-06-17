package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.game.Source;
import com.notjuststudio.engine2dgame.xml.ent.ObjectFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Created by George on 17.06.2017.
 */
public class Entity extends Resource  {

    private static Entity singleton = null;

    static Entity get() {
        if (singleton == null)
            singleton = new Entity();
        return singleton;
    }

    private Entity() {
        super("Entity", new ImageIcon(ImageLoader.loadImage(new File("Editor/res/entity.png"))));
    }

    @Override
    boolean loadResources(List<Source> sources) {
        final boolean result = super.loadResources(sources);
        Window.get().updateRow(4);
        return result;
    }

    @Override
    boolean loadResource(Source source) {
        final com.notjuststudio.engine2dgame.xml.ent.Entity tmp;
        try {
            tmp =
                    Parser.loadXml(source.getSource(), ObjectFactory.class, com.notjuststudio.engine2dgame.xml.ent.Entity.class);
        } catch (Parser.InvalidXmlException e) {
            System.err.println(e.getMessage());
            return false;
        }
        add(new DefaultMutableTreeNode(source.getId()));

        return true;
    }

    @Override
    Container openFile(String name) {
        return new JPanel();
    }

    @Override
    String getTypeName() {
        return "Entity";
    }
}
