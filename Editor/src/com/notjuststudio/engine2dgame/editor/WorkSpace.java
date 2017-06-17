package com.notjuststudio.engine2dgame.editor;

import javax.swing.*;

/**
 * Created by George on 17.06.2017.
 */
public class WorkSpace extends JTabbedPane {

    static final String SEPARATOR = "> ";

    private static WorkSpace singleton;

    static WorkSpace get() {
        if (singleton == null)
            singleton = new WorkSpace();
        return  singleton;
    }

    private WorkSpace() {
        super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    boolean hasTab(String name) {
        for (int i = 0; i < getTabCount(); i++) {
            if (getTitleAt(i).equals(name))
                return true;
        }
        return false;
    }

}
