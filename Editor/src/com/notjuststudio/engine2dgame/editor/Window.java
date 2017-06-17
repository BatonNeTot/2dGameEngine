package com.notjuststudio.engine2dgame.editor;

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

/**
 * Created by George on 15.06.2017.
 */
public class Window {

    private static Window singleton = null;

    static Window get() {
        if (singleton == null)
            singleton = new Window();
        return singleton;
    }

    private Window() {
        PyEngine.init();

        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        final int width = 800;
        final int height = 600;

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final int screenWidth = gd.getDisplayMode().getWidth();
        final int screenHeight = gd.getDisplayMode().getHeight();

        leafPopup = new JPopupMenu() {{
            add(new JMenuItem("Delete"));
        }};

        nodePopup = new JPopupMenu() {{
            final JPopupMenu popup = this;
            add(new JMenuItem("New") {{
                final JMenuItem me = this;
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(getPath(selectedNode));
                    }
                });
            }});
        }};

        spriteNode = new DefaultMutableTreeNode("Sprite");
        backgroundNode = new DefaultMutableTreeNode("Background");
        fontNode = new DefaultMutableTreeNode("Font");
        entityNode = new DefaultMutableTreeNode("Entity");
        roomNode = new DefaultMutableTreeNode("Room");

        window = new JFrame("Editor") {{

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    ((JSplitPane)getContentPane().getComponent(0)).resetToPreferredSizes();
                }
            });

            setJMenuBar(new JMenuBar() {{

                add(new JMenu("File") {{

                    setMnemonic('F');

                    add(new JMenuItem("New") {{
                        setAccelerator(KeyStroke.getKeyStroke("control N"));
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                            }
                        });
                    }});
                    add(new JMenuItem("Open") {{
                        setMnemonic('O');
                        setAccelerator(KeyStroke.getKeyStroke("control O"));
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Manager.openProject();
                            }
                        });
                    }});
                    add(new JMenuItem("Save") {{
                        setAccelerator(KeyStroke.getKeyStroke("control S"));
                    }});
                    add(new JMenuItem("Save all") {{
                        setAccelerator(KeyStroke.getKeyStroke("shift control S"));
                    }});
                    addSeparator();
                    add(new JMenuItem("Project parameters...") {{
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                            }
                        });
                    }});
                    addSeparator();
                    add(new JMenuItem("Exit") {{
                        Component me = this;
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                Window.close();
                            }
                        });

                    }});

                }});

                add(new JMenu("Edit") {{

                    setMnemonic('E');

                    add(new JMenuItem("Copy") {{
                        setAccelerator(KeyStroke.getKeyStroke("control C"));
                    }});
                    add(new JMenuItem("Paste") {{
                        setAccelerator(KeyStroke.getKeyStroke("control V"));
                    }});

                }});

            }});

            add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                            new JScrollPane(new JTree(new DefaultTreeModel(new DefaultMutableTreeNode(){{
                                add((MutableTreeNode)spriteNode);
                                add((MutableTreeNode)backgroundNode);
                                add((MutableTreeNode)fontNode);
                                add((MutableTreeNode)entityNode);
                                add((MutableTreeNode)roomNode);
                            }}, true)){{
                                final JTree me = this;
                                setRootVisible(true);
                                for (int i = getRowCount() - 1; i > 0; i--) {
                                    expandRow(i);
                                    collapseRow(i);
                                }
                                addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mousePressed ( MouseEvent e )
                                    {
                                        final TreePath path = me.getPathForLocation ( e.getX (), e.getY () );
                                        final Rectangle pathBounds = me.getUI ().getPathBounds ( me, path );
                                        if ( pathBounds != null && pathBounds.contains ( e.getX (), e.getY () ) ) {
                                            boolean flag = false;
                                            try {
                                                for (TreePath treePath : me.getSelectionPaths()) {
                                                    if (treePath.equals(path)) {
                                                        flag = true;
                                                        break;
                                                    }
                                                }
                                            } catch (NullPointerException n) {}
                                            if (!flag) {
                                                me.setSelectionPath(path);
                                            }

                                            if ( SwingUtilities.isRightMouseButton ( e ) ) {
                                                switch (path.getPath().length) {
                                                    case 2: {
                                                        selectedNode = (TreeNode)path.getLastPathComponent();
                                                        nodePopup.show(me, e.getX(), e.getY());
                                                        break;
                                                    }
                                                    case 3: {
                                                        selectedNode = (TreeNode)path.getLastPathComponent();
                                                        leafPopup.show(me, e.getX(), e.getY());
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            setSelectionPath(null);
                                        }
                                    }
                                });
                                addTreeExpansionListener(new TreeExpansionListener() {
                                    @Override
                                    public void treeExpanded(TreeExpansionEvent event) {
                                    }

                                    @Override
                                    public void treeCollapsed(TreeExpansionEvent event) {
                                        if (event.getPath().getPath().length == 1)
                                            me.expandRow(0);
                                    }
                                });
                                addTreeSelectionListener(new TreeSelectionListener() {
                                    @Override
                                    public void valueChanged(TreeSelectionEvent e) {
                                        if (e.getPath().getPath().length <= 2)
                                            me.setSelectionPaths(treePathBuffer);
                                        else
                                            treePathBuffer = me.getSelectionPaths();
                                    }
                                });
                            }}){{setBorder(new EtchedBorder());}},
                            new JScrollPane(new JPanel() {{

                            }}){{setBorder(new EtchedBorder());}}){{
                        setDividerLocation(150);
                    }},
                    new JScrollPane(Console.get())) {{
                setDividerLocation(height - 150);
            }});
        }};

        System.setOut(new PrintStream(Console.get().getOut()));
        System.setErr(new PrintStream(Console.get().getErr()));
        PyEngine.get().initConsole();

        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Window.close();
            }
        });

        //Display the window.
        window.setSize(width, height);
//        window.setLocation((screenWidth - width)/2, (screenHeight - height)/2);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void initLookAndFeel() {
        final String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {

            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException e) {
            System.err.println("Couldn't find class for specified look and feel:"
                    + lookAndFeel);
            System.err.println("Did you include the L&F library in the class path?");
            System.err.println("Using the default look and feel.");
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Can't use the specified look and feel ("
                    + lookAndFeel
                    + ") on this platform.");
            System.err.println("Using the default look and feel.");
        } catch (Exception e) {
            System.err.println("Couldn't get specified look and feel ("
                    + lookAndFeel
                    + "), for some reason.");
            System.err.println("Using the default look and feel.");
            e.printStackTrace();
        }
    }

    static void createAndShowGUI() {
        get();
    }

    TreePath[]
            treePathBuffer;

    final JFrame
            window;

    final JPopupMenu
            leafPopup,
            nodePopup;

    final TreeNode
            spriteNode,
            backgroundNode,
            fontNode,
            entityNode,
            roomNode;

    TreeNode
            selectedNode;

    boolean wasChanged = false;

    public static void close() {
        exit:
        if (get().wasChanged)
            switch (JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Save changes", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)) {
                case JOptionPane.YES_OPTION:
                case JOptionPane.NO_OPTION: {
                    break exit;
                }
                default:
                    return;
            }
        get().window.dispose();
    }

    static TreePath getPath(TreeNode treeNode) {
        final java.util.List<Object> nodes = new ArrayList<Object>();
        if (treeNode != null) {
            nodes.add(treeNode);
            treeNode = treeNode.getParent();
            while (treeNode != null) {
                nodes.add(0, treeNode);
                treeNode = treeNode.getParent();
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }
}
