package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.Parser;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by George on 13.06.2017.
 */
public class Console extends JTextPane {

    static Style normalStyle;
    static Style errStyle;

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

        errStyle = addStyle("errput", null);
        StyleConstants.setForeground(errStyle, Color.RED);
    }

    Console() {
        lastCmd.add("");
        getStyledDocument().setParagraphAttributes( 0,0, normalStyle, true);
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "backspace");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
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

                updateInputLine((bigCommand ? PyEngine.PREFIX_NEW : PyEngine.PREFIX) + lastCmd.get(lastCmd.size() - lastIndex));
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

                updateInputLine((bigCommand ? PyEngine.PREFIX_NEW : PyEngine.PREFIX) + lastCmd.get(lastCmd.size() - lastIndex));
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

    void updateInputLine(String string) {
        boolean moreThanOne = getLineCount() > 1;
        try {
            int position = getStartLinePosition() - (moreThanOne ? 1 : 0);
            getStyledDocument().remove(position, getDocument().getLength() - position);
            if (moreThanOne)
                append("\n", normalStyle);
            append(string, normalStyle);
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

    void append(String string) {
        append(string, null);
    }

    void append(String string, Style style) {
        append(string, getDocument().getLength(), style);
    }

    void append(String string, int offset, Style style) {
        try {
            getStyledDocument().insertString(offset, string, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(){

        try {
            Window.console.getStyledDocument().remove(0, Window.console.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        Window.console.updateInputLine(PyEngine.PREFIX);
    }

    void execCommand(String command) {
        commandBuffer += command + "\n";
        if (bigCommand) {
            if (command.replace(" ", "").isEmpty()) {
                bigCommand = false;
                waitNextTab = false;
                PyEngine.execCommand(commandBuffer);
                commandBuffer = "";
                updateInputLine(PyEngine.PREFIX);
                return;
            }
            if (waitNextTab) {
                waitNextTab = false;
                if (commandQueue.isEmpty() ? 0 < Parser.spacesInStart(command) : commandQueue.peek() < Parser.spacesInStart(command)) {
                    commandQueue.add(Parser.spacesInStart(command));
                } else {
                    bigCommand = false;
                    PyEngine.execCommand(commandBuffer);
                    commandBuffer = "";
                    updateInputLine(PyEngine.PREFIX);
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
                    PyEngine.execCommand(commandBuffer);
                    commandBuffer = "";
                    updateInputLine(PyEngine.PREFIX);
                    return;
                }
            }
            if (command.replace(" ", "").endsWith(":")) {
                waitNextTab = true;
            }
            updateInputLine(PyEngine.PREFIX_NEW);
        } else {
            if (command.replace(" ", "").endsWith(":")) {
                bigCommand = true;
                waitNextTab = true;
                updateInputLine(PyEngine.PREFIX_NEW);
            } else {
                PyEngine.execCommand(commandBuffer);
                commandBuffer = "";
                updateInputLine(PyEngine.PREFIX);
            }
        }
    }

    public static class ConsoleOut extends OutputStream {

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            Window.console.write(new String(b, off, len), normalStyle);
        }

        @Override
        public void write(int b) throws IOException {}
    }

    public static class ConsoleErr extends OutputStream {

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            Window.console.write(new String(b, off, len), errStyle);
        }

        @Override
        public void write(int b) throws IOException {}

    }

    private void write(String text, Style style) {
        text = text.replace(System.lineSeparator(), "\n");
        if (Window.console.getLineCount() <= 1)
            Window.console.append("\n", 0, style);
        int position = Window.console.getStartLinePosition() - 1;
        if (Window.console.needNextLine) {
            Window.console.append("\n", position, style);
            position += 1;
            Window.console.needNextLine = false;
        }
        if (text.endsWith("\n")) {
            Window.console.needNextLine = true;
            text = text.substring(0, text.lastIndexOf("\n"));
        }
        Window.console.append(text, position, style);
    }

}
