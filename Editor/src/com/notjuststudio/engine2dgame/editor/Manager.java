package com.notjuststudio.engine2dgame.editor;

/**
 * Created by Georgy on 14.05.2017.
 */
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;

public class Manager implements ActionListener {

    public void actionPerformed(ActionEvent e) {}

    private static void initLookAndFeel() {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
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

    private static void createAndShowGUI() {

        PyEngine.init();

        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        int width = 800;
        int height = 600;

        leafPopup = new JPopupMenu() {{
            add(new JMenuItem("Delete"));
        }};

        nodePopup = new JPopupMenu() {{
            JPopupMenu popup = this;
            add(new JMenuItem("New") {{
                JMenuItem me = this;
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println(popup.getInvoker());
                    }
                });
            }});
        }};

        frame = new JFrame("Editor") {{

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
                                System.out.println("new");
                            }
                        });
                    }});
                    add(new JMenuItem("Open") {{
                        setMnemonic('O');
                        setAccelerator(KeyStroke.getKeyStroke("control O"));
                    }});
                    add(new JMenuItem("Save") {{
                        setAccelerator(KeyStroke.getKeyStroke("control S"));
                    }});
                    add(new JMenuItem("Save all") {{
                        setAccelerator(KeyStroke.getKeyStroke("shift control S"));
                    }});
                    addSeparator();
                    add(new JMenuItem("Exit") {{
                        Component me = this;
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                PyEngine.exec("quit()");
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
                                add(new DefaultMutableTreeNode("Sprite"){{
                                    add(new DefaultMutableTreeNode("myhero", false));
                                }});
                                add(new DefaultMutableTreeNode("Background"));
                                add(new DefaultMutableTreeNode("Font"));
                                add(new DefaultMutableTreeNode("Entity"));
                                add(new DefaultMutableTreeNode("Room"));
                            }}, true)){{
                                JTree me = this;
                                setRootVisible(true);
                                addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mousePressed ( MouseEvent e )
                                    {
                                        if ( SwingUtilities.isRightMouseButton ( e ) )
                                        {
                                            TreePath path = me.getPathForLocation ( e.getX (), e.getY () );
                                            Rectangle pathBounds = me.getUI ().getPathBounds ( me, path );
                                            if ( pathBounds != null && pathBounds.contains ( e.getX (), e.getY () ) )
                                            {
                                                nodePopup.show (me, e.getX (), e.getY () );
                                            }
                                        }
                                    }
                                });
                            }}){{setBorder(new EtchedBorder());}},
                            new JScrollPane(new JPanel() {{

                            }}){{setBorder(new EtchedBorder());}}){{
                        setDividerLocation(150);
                    }},
                    new JScrollPane(
                            console = new Console())) {{
                setDividerLocation(height - 150);
            }});
        }};

        System.setOut(new PrintStream(new Console.ConsoleOut()));
        System.setErr(new PrintStream(new Console.ConsoleErr()));
        PyEngine.initConsole();

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PyEngine.exec("quit()");
            }
        });

        //Display the window.
        frame.setSize(width, height);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
//        frame.setLocation((screenWidth - width)/2, (screenHeight - height)/2);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static JFrame frame;
    static JPopupMenu leafPopup;
    static JPopupMenu nodePopup;
    static TreeNode selectedNode;
    static boolean wasChanged = false;
    static Console console;

    public static void close() {
        exit:
        if (wasChanged)
            switch (JOptionPane.showConfirmDialog(null, "Do you want to save changes?", "Save changes", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)) {
                case JOptionPane.YES_OPTION:
                case JOptionPane.NO_OPTION: {
                    break exit;
                }
                default:
                    return;
            }
        frame.dispose();
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
