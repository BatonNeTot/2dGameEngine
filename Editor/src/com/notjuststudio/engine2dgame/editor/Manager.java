package com.notjuststudio.engine2dgame.editor;

/**
 * Created by Georgy on 14.05.2017.
 */
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.util.ArrayList;

public class Manager {

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Window.createAndShowGUI();
            }
        });
    }
}
