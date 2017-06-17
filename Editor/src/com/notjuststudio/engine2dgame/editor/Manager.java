package com.notjuststudio.engine2dgame.editor;

/**
 * Created by Georgy on 14.05.2017.
 */
import com.notjuststudio.engine2dgame.util.Parser;
import com.notjuststudio.engine2dgame.xml.game.Game;

import javax.swing.JFileChooser;
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
        final int ret = fileChooser.showDialog(Window.get().window, "Open");
        if (ret == JFileChooser.APPROVE_OPTION)
            openProject(fileChooser.getSelectedFile());
    }

    public static void openProject(final String filePath) {
        openProject(new File(filePath));
    }

    static void openProject(final File file) {
        final Game gameKeeper;
        try {
            gameKeeper =
                    Parser.loadXml(file.getPath(), com.notjuststudio.engine2dgame.xml.game.ObjectFactory.class, Game.class);
        } catch (Parser.InvalidXmlException e) {
            System.err.println("Can't load project " + file.getPath());
            return;
        }
        boolean flag = true;

        flag &= Sprite.get().loadResources(gameKeeper.getSprite());
        flag &= Background.get().loadResources(gameKeeper.getBackground());
        flag &= Font.get().loadResources(gameKeeper.getFont());
        flag &= Entity.get().loadResources(gameKeeper.getEntity());
        flag &= Room.get().loadResources(gameKeeper.getRoom());

        if (flag) {
            System.out.println("Successful load");
        } else {
            System.out.println("Project was not fully load");
        }
    }

    public static void main(final String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> {
            Window.createAndShowGUI();
            if (args.length >= 1) {
                Manager.openProject(args[0]);
            }
        });
    }
}
