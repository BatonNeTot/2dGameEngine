package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.Parser;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

/**
 * Created by George on 13.06.2017.
 */
public class Console extends JTextPane {

    static Style normalStyle;
    static Style editStyle;
    static Style errStyle;

    OutputStream
        out,
        err;

    boolean needNextLine = false;

    final java.util.List<String> lastCmd = new ArrayList<>();
    private int lastIndex = 1;

    private String commandBuffer = "";
    private final Queue<Integer> commandQueue = Collections.asLifoQueue(new ArrayDeque<>());
    private boolean waitNextTab = false;
    private boolean bigCommand = false;

    {
        normalStyle = addStyle("output", null);
        StyleConstants.setForeground(normalStyle, Color.BLACK);

        editStyle = addStyle("editput", null);
        StyleConstants.setForeground(editStyle, Color.BLUE);

        errStyle = addStyle("errput", null);
        StyleConstants.setForeground(errStyle, Color.RED);
    }

    public OutputStream getOut() {
        return out;
    }

    public OutputStream getErr() {
        return err;
    }

    private static Console singleton = null;

    static Console get() {
        if (singleton == null)
            singleton = new Console();
        return singleton;
    }

    private Console() {
        out = new ConsoleOut();
        err = new ConsoleErr();
        lastCmd.add("");
        getStyledDocument().setParagraphAttributes( 0,0, normalStyle, true);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tab");
        getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = getCommand();

                if (!command.replace(" ", "").isEmpty()) {
                    lastCmd.set(lastCmd.size() - 1, command);

                    if (lastIndex > 1)
                        lastCmd.remove(lastCmd.size() - lastIndex);
                    lastCmd.add("");
                    lastIndex = 1;
                    lastCmd.set(lastCmd.size() - lastIndex, command);
                }

                append("\n");
                needNextLine = true;
                execCommand(command);
                setCaretPosition(getStartLinePosition() + 4);
            }
        });
        getActionMap().put("backspace", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getCaretPosition() > getStartLinePosition() + 4)
                    backspace();
            }
        });
        getActionMap().put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastIndex >= lastCmd.size())
                    return;

                String command = getCommand();
                lastCmd.set(lastCmd.size() - lastIndex, command);
                lastIndex++;

                updateInputLine(lastCmd.get(lastCmd.size() - lastIndex));
            }
        });
        getActionMap().put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (lastIndex <= 1)
                    return;

                String command = getCommand();
                lastCmd.set(lastCmd.size() - lastIndex, command);
                lastIndex--;

                updateInputLine(lastCmd.get(lastCmd.size() - lastIndex));
            }
        });
        getActionMap().put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCaretPosition(Math.max(getCaretPosition() - 1, getStartLinePosition() + 4));
            }
        });
        getActionMap().put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCaretPosition(Math.max(Math.min(getCaretPosition() + 1, getText().length()), getStartLinePosition() + 4));
            }
        });
        getActionMap().put("tab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String command = getCommand();
                final String[] keysI = get().getKeysI(command);
                if (keysI.length == 1) {
                    updateInputLine(command.substring(0, Math.max(0, command.lastIndexOf('.'))) + (command.indexOf('.') >= 0 ? "." : "") + keysI[0]);
                    return;
                }
                final String[] keys = get().getKeys(command);
                final String potentialCommand = Parser.findEqualStart(keys);
                if (command.substring(command.lastIndexOf('.') + 1).equals(potentialCommand)) {
                    if (keys.length > 1){
                        append("\n");
                        needNextLine = true;
                        System.out.println(Parser.formatKeys(keys));
                    }
                    updateInputLine(command.substring(0, Math.max(0, command.lastIndexOf('.'))) + (command.indexOf('.') >= 0 ? "." : "") + potentialCommand);
                } else if (potentialCommand.startsWith(command.substring(command.lastIndexOf('.') + 1))) {
                    updateInputLine(command.substring(0, Math.max(0, command.lastIndexOf('.'))) + (command.indexOf('.') >= 0 ? "." : "") + potentialCommand);
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (getCaretPosition() < getStartLinePosition() + 4)
                    e.consume();
                else
                    super.keyTyped(e);
            }
        });
    }

    void updateInputLine() {
        updateInputLine("");
    }

    void updateInputLine(final String string) {
        boolean moreThanOne = getLineCount() > 1;
        try {
            int position = getStartLinePosition() - (moreThanOne ? 1 : 0);
            getStyledDocument().remove(position, getDocument().getLength() - position);
            if (moreThanOne)
                append("\n", normalStyle);
            append((bigCommand ? PyEngine.PREFIX_NEW : PyEngine.PREFIX), normalStyle);
            append(" " + string, editStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getText() {
        return super.getText().replace(System.lineSeparator(), "\n");
    }

    int getLineCount() {
        int result = 1;
        int index = 0;
        while((index = getText().indexOf('\n', index + 1)) >= 0) {
            result++;
        }
        return result;
    }

    int getStartLinePosition() {
        return getText().lastIndexOf('\n') + 1;
    }


    String getCommand() {
        String[] lines = getText().split("\n");
        return lines[lines.length - 1].substring(4);
    }

    void backspace() {
        try {
            getStyledDocument().remove(getCaretPosition() - 1, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void append(final String string) {
        append(string, null);
    }

    void append(final String string, Style style) {
        append(string, getDocument().getLength(), style);
    }

    void append(final String string, int offset, Style style) {
        try {
            getStyledDocument().insertString(offset, string, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String[] getKeysI(String command) {
        final String[] keys = PyEngine.get().getDir(command.substring(0, Math.max(0, command.lastIndexOf('.'))));
        final String commandEnd = command.substring(command.lastIndexOf('.') + 1);
        final List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (key.toLowerCase().startsWith(commandEnd.toLowerCase()))
                list.add(key);
        }
        return list.toArray(new String[list.size()]);
    }

    String[] getKeys(String command) {
        final String[] keys = PyEngine.get().getDir(command.substring(0, Math.max(0, command.lastIndexOf('.'))));
        final String commandEnd = command.substring(command.lastIndexOf('.') + 1);
        final List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (key.startsWith(commandEnd))
                list.add(key);
        }
        list.sort(String::compareTo);
        return list.toArray(new String[list.size()]);
    }

    public static void clear(){

        try {
            get().getStyledDocument().remove(0, get().getDocument().getLength());
            get().needNextLine = false;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        get().updateInputLine();
    }

    void execCommand(final String command) {
        commandBuffer += command + "\n";
        if (bigCommand) {
            if (command.replace(" ", "").isEmpty()) {
                bigCommand = false;
                waitNextTab = false;
                PyEngine.get().execCommand(commandBuffer);
                commandBuffer = "";
                updateInputLine();
                return;
            }
            if (waitNextTab) {
                waitNextTab = false;
                if (commandQueue.isEmpty() ? 0 < Parser.spacesInStart(command) : commandQueue.peek() < Parser.spacesInStart(command)) {
                    commandQueue.add(Parser.spacesInStart(command));
                } else {
                    bigCommand = false;
                    PyEngine.get().execCommand(commandBuffer);
                    commandBuffer = "";
                    updateInputLine();
                    return;
                }
            } else {
                while (!commandQueue.isEmpty()) {
                    if (commandQueue.peek() == Parser.spacesInStart(command)) {
                        break;
                    }
                    commandQueue.remove();
                }
                if (commandQueue.isEmpty()) {
                    bigCommand = false;
                    PyEngine.get().execCommand(commandBuffer);
                    commandBuffer = "";
                    updateInputLine();
                    return;
                }
            }
            if (command.replace(" ", "").endsWith(":")) {
                waitNextTab = true;
            }
            updateInputLine();
        } else {
            if (command.replace(" ", "").endsWith(":")) {
                bigCommand = true;
                waitNextTab = true;
                updateInputLine();
            } else {
                PyEngine.get().execCommand(commandBuffer);
                commandBuffer = "";
                updateInputLine();
            }
        }
    }

    public class ConsoleOut extends OutputStream {

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            get().write(new String(b, off, len), normalStyle);
        }

        @Override
        public void write(int b) throws IOException {}
    }

    public class ConsoleErr extends OutputStream {

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            get().write(new String(b, off, len), errStyle);
        }

        @Override
        public void write(int b) throws IOException {}

    }

    private void write(String text, final Style style) {
        text = text.replace(System.lineSeparator(), "\n");
        if (get().getLineCount() <= 1)
            get().append("\n", 0, style);
        int position = get().getStartLinePosition() - 1;
        if (get().needNextLine) {
            get().append("\n", position, style);
            position += 1;
            get().needNextLine = false;
        }
        if (text.endsWith("\n")) {
            get().needNextLine = true;
            text = text.substring(0, text.lastIndexOf("\n"));
        }
        get().append(text, position, style);
    }

}
