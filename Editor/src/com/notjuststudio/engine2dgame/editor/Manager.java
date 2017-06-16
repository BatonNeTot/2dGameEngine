package com.notjuststudio.engine2dgame.editor;

/**
 * Created by Georgy on 14.05.2017.
 */
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.game.Game;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Manager {

    public static void openProject() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Open project");
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".xml") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Game extension (.xml)";
            }
        });
        int ret = fileChooser.showDialog(Window.window, "Open");
        if (ret == JFileChooser.APPROVE_OPTION)
            openProject(fileChooser.getSelectedFile());
    }

    public static void openProject(String filePath) {
        openProject(new File(filePath));
    }

    static void openProject(File file) {
        Game gameKeeper =
                Parser.loadXml(file.getPath(), com.notjuststudio.engine2dgame.xml.game.ObjectFactory.class, Game.class);
        if (gameKeeper == null)
            System.err.println("Неполучилося");
            else
            System.out.println("Все збс");
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Window.createAndShowGUI();
                System.out.print(("Все".toCharArray().length) + "\n");
                System.out.println("Все");
            }
        });
    }
}
