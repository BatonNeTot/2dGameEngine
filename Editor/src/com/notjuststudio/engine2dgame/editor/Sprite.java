package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.game.Source;
import com.notjuststudio.engine2dgame.xml.spr.ObjectFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by George on 17.06.2017.
 */
public class Sprite extends Resource {

    private Map<String, com.notjuststudio.engine2dgame.xml.spr.Sprite> templates = new HashMap<>();

    private static Sprite singleton = null;

    static Sprite get() {
        if (singleton == null)
            singleton = new Sprite();
        return singleton;
    }

    private Sprite() {
        super("Sprite", new ImageIcon(ImageLoader.loadImage(new File("Editor/res/sprite.png"))));
    }

    @Override
    boolean loadResources(final List<Source> sources) {
        final boolean result = super.loadResources(sources);
        Window.get().updateRow(1);
        return result;
    }

    @Override
    boolean loadResource(final Source source) {
        final com.notjuststudio.engine2dgame.xml.spr.Sprite tmp;
        try {
            tmp =
                    Parser.loadXml(source.getSource(), ObjectFactory.class, com.notjuststudio.engine2dgame.xml.spr.Sprite.class);
        } catch (Parser.InvalidXmlException e) {
            System.err.println(e.getMessage());
            return false;
        }
        createNode(source.getId());
        templates.put(source.getId(), tmp);
        return true;
    }

    @Override
    Container openFile(String name) {
        return new JPanel();
    }

    @Override
    String getTypeName() {
        return "Sprite";
    }
}
