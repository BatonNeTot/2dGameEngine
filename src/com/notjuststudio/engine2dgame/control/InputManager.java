package com.notjuststudio.engine2dgame.control;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by George on 07.03.2017.
 */
public class InputManager {

    public static final int MOUSE_LEFT = 0;
    public static final int MOUSE_RIGHT = 1;
    public static final int MOUSE_MIDDLE = 2;
    public static final int MOUSE_4 = 4;
    public static final int MOUSE_5 = 3;

    public static final int KEY_NONE = 0;
    public static final int KEY_ESCAPE = 1;
    public static final int KEY_1 = 2;
    public static final int KEY_2 = 3;
    public static final int KEY_3 = 4;
    public static final int KEY_4 = 5;
    public static final int KEY_5 = 6;
    public static final int KEY_6 = 7;
    public static final int KEY_7 = 8;
    public static final int KEY_8 = 9;
    public static final int KEY_9 = 10;
    public static final int KEY_0 = 11;
    public static final int KEY_MINUS = 12;
    public static final int KEY_EQUALS = 13;
    public static final int KEY_BACK = 14;
    public static final int KEY_TAB = 15;
    public static final int KEY_Q = 16;
    public static final int KEY_W = 17;
    public static final int KEY_E = 18;
    public static final int KEY_R = 19;
    public static final int KEY_T = 20;
    public static final int KEY_Y = 21;
    public static final int KEY_U = 22;
    public static final int KEY_I = 23;
    public static final int KEY_O = 24;
    public static final int KEY_P = 25;
    public static final int KEY_LBRACKET = 26;
    public static final int KEY_RBRACKET = 27;
    public static final int KEY_RETURN = 28;
    public static final int KEY_LCONTROL = 29;
    public static final int KEY_A = 30;
    public static final int KEY_S = 31;
    public static final int KEY_D = 32;
    public static final int KEY_F = 33;
    public static final int KEY_G = 34;
    public static final int KEY_H = 35;
    public static final int KEY_J = 36;
    public static final int KEY_K = 37;
    public static final int KEY_L = 38;
    public static final int KEY_SEMICOLON = 39;
    public static final int KEY_APOSTROPHE = 40;
    public static final int KEY_GRAVE = 41;
    public static final int KEY_LSHIFT = 42;
    public static final int KEY_BACKSLASH = 43;
    public static final int KEY_Z = 44;
    public static final int KEY_X = 45;
    public static final int KEY_C = 46;
    public static final int KEY_V = 47;
    public static final int KEY_B = 48;
    public static final int KEY_N = 49;
    public static final int KEY_M = 50;
    public static final int KEY_COMMA = 51;
    public static final int KEY_PERIOD = 52;
    public static final int KEY_SLASH = 53;
    public static final int KEY_RSHIFT = 54;
    public static final int KEY_MULTIPLY = 55;
    public static final int KEY_LMENU = 56;
    public static final int KEY_SPACE = 57;
    public static final int KEY_CAPITAL = 58;
    public static final int KEY_F1 = 59;
    public static final int KEY_F2 = 60;
    public static final int KEY_F3 = 61;
    public static final int KEY_F4 = 62;
    public static final int KEY_F5 = 63;
    public static final int KEY_F6 = 64;
    public static final int KEY_F7 = 65;
    public static final int KEY_F8 = 66;
    public static final int KEY_F9 = 67;
    public static final int KEY_F10 = 68;
    public static final int KEY_NUMLOCK = 69;
    public static final int KEY_SCROLL = 70;
    public static final int KEY_NUMPAD7 = 71;
    public static final int KEY_NUMPAD8 = 72;
    public static final int KEY_NUMPAD9 = 73;
    public static final int KEY_SUBTRACT = 74;
    public static final int KEY_NUMPAD4 = 75;
    public static final int KEY_NUMPAD5 = 76;
    public static final int KEY_NUMPAD6 = 77;
    public static final int KEY_ADD = 78;
    public static final int KEY_NUMPAD1 = 79;
    public static final int KEY_NUMPAD2 = 80;
    public static final int KEY_NUMPAD3 = 81;
    public static final int KEY_NUMPAD0 = 82;
    public static final int KEY_DECIMAL = 83;
    public static final int KEY_F11 = 87;
    public static final int KEY_F12 = 88;
    public static final int KEY_F13 = 100;
    public static final int KEY_F14 = 101;
    public static final int KEY_F15 = 102;
    public static final int KEY_F16 = 103;
    public static final int KEY_F17 = 104;
    public static final int KEY_F18 = 105;
    public static final int KEY_KANA = 112;
    public static final int KEY_F19 = 113;
    public static final int KEY_CONVERT = 121;
    public static final int KEY_NOCONVERT = 123;
    public static final int KEY_YEN = 125;
    public static final int KEY_NUMPADEQUALS = 141;
    public static final int KEY_CIRCUMFLEX = 144;
    public static final int KEY_AT = 145;
    public static final int KEY_COLON = 146;
    public static final int KEY_UNDERLINE = 147;
    public static final int KEY_KANJI = 148;
    public static final int KEY_STOP = 149;
    public static final int KEY_AX = 150;
    public static final int KEY_UNLABELED = 151;
    public static final int KEY_NUMPADENTER = 156;
    public static final int KEY_RCONTROL = 157;
    public static final int KEY_SECTION = 167;
    public static final int KEY_NUMPADCOMMA = 179;
    public static final int KEY_DIVIDE = 181;
    public static final int KEY_SYSRQ = 183;
    public static final int KEY_RMENU = 184;
    public static final int KEY_FUNCTION = 196;
    public static final int KEY_PAUSE = 197;
    public static final int KEY_HOME = 199;
    public static final int KEY_UP = 200;
    public static final int KEY_PRIOR = 201;
    public static final int KEY_LEFT = 203;
    public static final int KEY_RIGHT = 205;
    public static final int KEY_END = 207;
    public static final int KEY_DOWN = 208;
    public static final int KEY_NEXT = 209;
    public static final int KEY_INSERT = 210;
    public static final int KEY_DELETE = 211;
    public static final int KEY_CLEAR = 218;
    public static final int KEY_LMETA = 219;
    /** @deprecated */
    public static final int KEY_LWIN = 219;
    public static final int KEY_RMETA = 220;
    /** @deprecated */
    public static final int KEY_RWIN = 220;
    public static final int KEY_APPS = 221;
    public static final int KEY_POWER = 222;
    public static final int KEY_SLEEP = 223;

    private static final Set<Integer> keyTouched = new TreeSet<>();
    private static final Set<Integer> keyPressed = new TreeSet<>();
    private static final Set<Integer> keyReleased = new TreeSet<>();

    private static final Set<Integer> mouseTouched = new TreeSet<>();
    private static final Set<Integer> mousePressed = new TreeSet<>();
    private static final Set<Integer> mouseReleased = new TreeSet<>();

    static void update() {
        Set<Integer> wasPressed = new TreeSet<>();
        Set<Integer> wasReleased = new TreeSet<>();

        //Keyboard update

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                wasPressed.add(Keyboard.getEventKey());
            } else {
                wasReleased.add(Keyboard.getEventKey());
            }
        }

        keyReleased.clear();
        keyReleased.addAll(wasReleased);

        Set<Integer> tmp = new TreeSet<>(keyPressed);

        for (int key : tmp) {
            if (!Keyboard.isKeyDown(key)) {
                keyPressed.remove(key);
                keyReleased.add(key);
            }
        }

        keyPressed.addAll(keyTouched);
        keyPressed.removeAll(wasReleased);

        keyTouched.clear();
        keyTouched.addAll(wasPressed);

        wasPressed.clear();
        wasReleased.clear();

        //Mouse update

        while (Mouse.next()) {
            if (Mouse.getEventButton() != -1)
                if (Mouse.getEventButtonState()) {
                    wasPressed.add(Mouse.getEventButton());
                } else {
                    wasReleased.add(Mouse.getEventButton());
                }
        }

        mouseReleased.clear();
        mouseReleased.addAll(wasReleased);

        tmp = new TreeSet<>(mousePressed);

        for (int key : tmp) {
            if (!Mouse.isButtonDown(key)) {
                mousePressed.remove(key);
                mouseReleased.add(key);
            }
        }

        mousePressed.addAll(mouseTouched);
        mousePressed.removeAll(wasReleased);

        mouseTouched.clear();
        mouseTouched.addAll(wasPressed);
    }

    public static Set<Integer> getKeyTouched() {
        return new TreeSet<Integer>(keyTouched);
    }

    public static Set<Integer> getKeyPressed() {
        return new TreeSet<Integer>(keyPressed);
    }

    public static Set<Integer> getKeyReleased() {
        return new TreeSet<Integer>(keyReleased);
    }

    public static boolean isKeyTouched(int key) {
        return keyTouched.contains(key);
    }

    public static boolean isKeyPressed(int key) {
        return keyPressed.contains(key);
    }

    public static boolean isKeyReleased(int key) {
        return keyReleased.contains(key);
    }

    public static Set<Integer> getMouseTouched() {
        return new TreeSet<Integer>(mouseTouched);
    }

    public static Set<Integer> getMousePressed() {
        return new TreeSet<Integer>(mousePressed);
    }

    public static Set<Integer> getMouseReleased() {
        return new TreeSet<Integer>(mouseReleased);
    }

    public static boolean isMouseTouched(int key) {
        return mouseTouched.contains(key);
    }

    public static boolean isMousePressed(int key) {
        return mousePressed.contains(key);
    }

    public static boolean isMouseReleased(int key) {
        return mouseReleased.contains(key);
    }

    public static int getMouseX() {return Mouse.getX();}

    public static int getMouseY() {return Mouse.getY();}

    public static char getChar(int key) {
        char result;
        switch (key) {
            case KEY_1: result = '1'; break;
            case KEY_2: result = '2'; break;
            case KEY_3: result = '3'; break;
            case KEY_4: result = '4'; break;
            case KEY_5: result = '5'; break;
            case KEY_6: result = '6'; break;
            case KEY_7: result = '7'; break;
            case KEY_8: result = '8'; break;
            case KEY_9: result = '9'; break;
            case KEY_0: result = '0'; break;
            case KEY_Q: result = 'q'; break;
            case KEY_W: result = 'w'; break;
            case KEY_E: result = 'e'; break;
            case KEY_R: result = 'r'; break;
            case KEY_T: result = 't'; break;
            case KEY_Y: result = 'y'; break;
            case KEY_U: result = 'u'; break;
            case KEY_I: result = 'i'; break;
            case KEY_O: result = 'o'; break;
            case KEY_P: result = 'p'; break;
            case KEY_A: result = 'a'; break;
            case KEY_S: result = 's'; break;
            case KEY_D: result = 'd'; break;
            case KEY_F: result = 'f'; break;
            case KEY_G: result = 'g'; break;
            case KEY_H: result = 'h'; break;
            case KEY_J: result = 'j'; break;
            case KEY_K: result = 'k'; break;
            case KEY_L: result = 'l'; break;
            case KEY_Z: result = 'z'; break;
            case KEY_X: result = 'x'; break;
            case KEY_C: result = 'c'; break;
            case KEY_V: result = 'v'; break;
            case KEY_B: result = 'b'; break;
            case KEY_N: result = 'n'; break;
            case KEY_M: result = 'm'; break;
            case KEY_LBRACKET: result = '['; break;
            case KEY_RBRACKET: result = ']'; break;
            case KEY_SEMICOLON: result = ';'; break;
            case KEY_APOSTROPHE: result = '\''; break;
            case KEY_BACKSLASH: result = '\\'; break;
            case KEY_COMMA: result = ','; break;
            case KEY_PERIOD: result = '.'; break;
            case KEY_SLASH: result = '/'; break;
            case KEY_SPACE: result = ' '; break;
            case KEY_MINUS: result = '-'; break;
            case KEY_EQUALS: result = '='; break;
            default: result = '?'; break;
        }
        return result;
    }

    public static char getUpperChar(int key) {
        char result;
        switch (key) {
            case KEY_1: result = '!'; break;
            case KEY_2: result = '@'; break;
            case KEY_3: result = '#'; break;
            case KEY_4: result = '$'; break;
            case KEY_5: result = '%'; break;
            case KEY_6: result = '^'; break;
            case KEY_7: result = '&'; break;
            case KEY_8: result = '*'; break;
            case KEY_9: result = '('; break;
            case KEY_0: result = ')'; break;
            case KEY_Q: result = 'Q'; break;
            case KEY_W: result = 'W'; break;
            case KEY_E: result = 'E'; break;
            case KEY_R: result = 'R'; break;
            case KEY_T: result = 'T'; break;
            case KEY_Y: result = 'Y'; break;
            case KEY_U: result = 'U'; break;
            case KEY_I: result = 'I'; break;
            case KEY_O: result = 'O'; break;
            case KEY_P: result = 'P'; break;
            case KEY_A: result = 'A'; break;
            case KEY_S: result = 'S'; break;
            case KEY_D: result = 'D'; break;
            case KEY_F: result = 'F'; break;
            case KEY_G: result = 'G'; break;
            case KEY_H: result = 'H'; break;
            case KEY_J: result = 'J'; break;
            case KEY_K: result = 'K'; break;
            case KEY_L: result = 'L'; break;
            case KEY_Z: result = 'Z'; break;
            case KEY_X: result = 'X'; break;
            case KEY_C: result = 'C'; break;
            case KEY_V: result = 'V'; break;
            case KEY_B: result = 'B'; break;
            case KEY_N: result = 'N'; break;
            case KEY_M: result = 'M'; break;
            case KEY_LBRACKET: result = '{'; break;
            case KEY_RBRACKET: result = '}'; break;
            case KEY_SEMICOLON: result = ':'; break;
            case KEY_APOSTROPHE: result = '"'; break;
            case KEY_BACKSLASH: result = '|'; break;
            case KEY_COMMA: result = '<'; break;
            case KEY_PERIOD: result = '>'; break;
            case KEY_SLASH: result = '?'; break;
            case KEY_SPACE: result = ' '; break;
            case KEY_MINUS: result = '_'; break;
            case KEY_EQUALS: result = '+'; break;
            default: result = '?'; break;
        }
        return result;
    }

    public static boolean isKeyAllowed(int key) {
        return getChar(key) != '?';
    }
}
