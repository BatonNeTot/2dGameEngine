package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.xml.game.Source;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by George on 17.06.2017.
 */
public abstract class Resource extends DefaultMutableTreeNode{

    private Icon icon;

    boolean loadResources(final List<Source> sources) {
        boolean flag = true;
        for (Source source : sources) {
            flag &= loadResource(source);
        }
        sortNodes();
        return flag;
    }

    abstract boolean loadResource(final Source source);

    void createNode(final String name) {
        final TreeNode leaf = new DefaultMutableTreeNode(name);
        add((MutableTreeNode) leaf);
    }

    abstract Container openFile(String name);

    void sortNodes() {
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            nodes.add((DefaultMutableTreeNode)getChildAt(i));
        }
        nodes.sort(new Comparator<DefaultMutableTreeNode>() {
            @Override
            public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
                return ((String)o1.getUserObject()).compareTo((String)o2.getUserObject());
            }
        });
        for (int i = 0; i < getChildCount(); i++) {
            insert(nodes.get(i), i);
        }
    }

    Resource(final String name, final Icon icon) {
        super(name);
        this.icon = icon;
    }

    abstract String getTypeName();

    Icon getIcon(String name) {
        return icon;
    }

    Icon getIcon(){
        return icon;
    }

}
