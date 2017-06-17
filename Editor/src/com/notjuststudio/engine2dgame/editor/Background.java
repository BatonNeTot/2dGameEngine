package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.ImageLoader;
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.back.ObjectFactory;
import com.notjuststudio.engine2dgame.xml.game.Source;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Created by George on 17.06.2017.
 */
public class Background extends Resource  {

    private static Background singleton = null;

    static Background get() {
        if (singleton == null)
            singleton = new Background();
        return singleton;
    }

    private Background() {
        super("Background", new ImageIcon(ImageLoader.loadImage(new File("Editor/res/background.png"))));
    }

    @Override
    boolean loadResources(List<Source> sources) {
        final boolean result = super.loadResources(sources);
        Window.get().updateRow(2);
        return result;
    }

    @Override
    boolean loadResource(Source source) {
        final com.notjuststudio.engine2dgame.xml.back.Background tmp;
        try {
            tmp =
                    Parser.loadXml(source.getSource(), ObjectFactory.class, com.notjuststudio.engine2dgame.xml.back.Background.class);
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
        return "Background";
    }
}
