package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

        UIManager.put("MenuItem.acceleratorForeground", Color.GRAY);

        UIManager.put("Editor.newFileIcon", new ImageIcon(ImageLoader.loadImage(new File("Editor/res/newFile.png"))));
        UIManager.put("Editor.openFileIcon", new ImageIcon(ImageLoader.loadImage(new File("Editor/res/openFile.png"))));
        UIManager.put("Editor.saveFileIcon", new ImageIcon(ImageLoader.loadImage(new File("Editor/res/saveFile.png"))));

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

        tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode(){{
            add(Sprite.get());
            add(Background.get());
            add(Font.get());
            add(Entity.get());
            add(Room.get());
        }})){{
            final JTree me = this;
            setRootVisible(true);
            setCellRenderer(new DefaultTreeCellRenderer() {
                @Override
                public Component getTreeCellRendererComponent(JTree tree,
                                                              Object value, boolean selected, boolean expanded,
                                                              boolean isLeaf, int row, boolean focused) {
                    Component c = super.getTreeCellRendererComponent(tree, value,
                            selected, expanded, isLeaf, row, focused);
                    if (tree.getModel().getRoot().equals(value))
                        setIcon(null);
                    else if (value instanceof Resource)
                        setIcon(((Resource)value).getIcon());
                    else {
                        TreeNode parent = ((TreeNode)value).getParent();
                        if (parent instanceof Resource) {
                            final Resource resource = (Resource) parent;
                            setIcon(resource.getIcon((String)((DefaultMutableTreeNode)value).getUserObject()));
                        }
                    }
                    return c;
                }
            });
            addMouseListener(new MouseAdapter() {

                TreePath getPath(MouseEvent e) {
                    final TreePath path = me.getPathForLocation ( e.getX (), e.getY () );
                    final Rectangle pathBounds = me.getUI ().getPathBounds ( me, path );
                    return pathBounds != null && pathBounds.contains ( e.getX (), e.getY () ) ? path : null;
                }

                @Override
                public void mousePressed ( MouseEvent e )
                {
                    final TreePath path = getPath(e);
                    if (path != null) {
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

                void mouseDoubleClicked(MouseEvent e) {
                    final TreePath path = getPath(e);
                    if (path != null && path.getPath().length == 3) {
                        final Resource type = ((Resource)path.getPath()[1]);
                        final String name = (String)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
                        if (!WorkSpace.get().hasTab(name))
                            WorkSpace.get().addTab(name, type.getIcon(), type.openFile(name));

                    }
                }

                boolean isAlreadyOneClick;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isAlreadyOneClick) {
                        timer.cancel();
                        mouseDoubleClicked(e);
                        isAlreadyOneClick = false;
                    } else {
                        isAlreadyOneClick = true;
                        timer = new Timer("doubleclickTimer", false);
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isAlreadyOneClick = false;
                                timer.cancel();
                            }
                        }, (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"));
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
        }};

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
                        setIcon(UIManager.getIcon("Editor.newFileIcon"));
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {

                            }
                        });
                    }});
                    add(new JMenuItem("Open") {{
                        setMnemonic('O');
                        setAccelerator(KeyStroke.getKeyStroke("control O"));
                        setIcon(UIManager.getIcon("Editor.openFileIcon"));
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Manager.openProject();
                            }
                        });
                    }});
                    add(new JMenuItem("Save") {{
                        setAccelerator(KeyStroke.getKeyStroke("control S"));
                        setIcon(UIManager.getIcon("Editor.saveFileIcon"));
                    }});
                    add(new JMenuItem("Save all") {{
                        setAccelerator(KeyStroke.getKeyStroke("shift control S"));
                        setIcon(UIManager.getIcon("Editor.saveFileIcon"));
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
                            new JScrollPane(tree){{
                                setBorder(new EtchedBorder());
                    }}, WorkSpace.get() ){{
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

    void updateRow(int row) {
        tree.expandRow(row);
        tree.collapseRow(row);
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

    Timer
            timer = null;

    TreePath[]
            treePathBuffer;

    final JFrame
            window;

    final JPopupMenu
            leafPopup,
            nodePopup;

    final JTree
            tree;
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
