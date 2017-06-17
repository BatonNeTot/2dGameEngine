package com.notjuststudio.engine2dgame.editor;

import com.notjuststudio.engine2dgame.util.Parser;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Georgy on 31.03.2017.
 */
public class PyEngine {

    private static PyEngine singleton = null;

    public final static PrintStream out;
    public final static PrintStream err;

    public static final String
            PREFIX = ">>>",
            PREFIX_NEW = "...";

    private final PythonInterpreter pyInterpreter;

    static {
        out = System.out;
        err = System.err;
    }

    static PyEngine get() {
        if (singleton == null)
            singleton = new PyEngine();
        return singleton;
    }

    private PyEngine() {
        pyInterpreter = new PythonInterpreter();
        pyInterpreter.setOut(System.out);
        pyInterpreter.setErr(System.err);
        loadClasses(PyEngine.class);
        exec("import math");
        exec("from java.awt import Color");
        exec("from java.lang import Float");
        exec("from java.lang import String");
        exec("exit = quit = Window.close");
        exec("clear = Console.clear");
    }

    static void init() {
        get();
    }

    void initConsole() {
        pyInterpreter.setOut(Console.get().getOut());
        pyInterpreter.setErr(Console.get().getErr());
        Console.get().updateInputLine();
    }

    String[] getDir() {
        return getDir("");
    }

    String[] getDir(final String name) {
        if (name.isEmpty()) {
            final Set<Object> keySet = ((PyStringMap) pyInterpreter.getLocals()).getMap().keySet();
            return keySet.toArray(new String[keySet.size()]);
        } else {
            try {
                return getDir(get(name));
            } catch (PyException e) {
                return new String[]{};
            }
        }
    }

    String[] getDir(final PyObject object) {
        final List<String> list = (PyList) object.__dir__();
        final String[] pythons = list.toArray(new String[list.size()]);
        if (isJava(object)) {
            final Set<String> javas = new HashSet<String>(){{
                addAll(Arrays.asList(Parser.getDir(object.__tojava__(Object.class))));
                addAll(list);
            }};
            return javas.toArray(new String[javas.size()]);
        } else {
            return pythons;
        }
    }

    PyObject get(final String fullName) {
        return pyInterpreter.eval(fullName);
    }

    boolean isJava(final PyObject object) {
        return object.__str__().toString().indexOf("__main__") < 0;
    }

    void exec(final String cmd) {
        pyInterpreter.exec(cmd);
    }

    void exec(final PyCode script) {
        pyInterpreter.exec(script);
    }

    PyObject eval(final String cmd) {
        return pyInterpreter.eval(cmd);
    }

    void put(final String name, final Object value) {
        pyInterpreter.set(name, value);
    }

    <T> T get(final String name, final Class<T> javaClass) {
        return pyInterpreter.get(name, javaClass);
    }

    PyCode compile(final String script) {
        return pyInterpreter.compile(script);
    }

    void loadClasses(final Class mClass) {
        final String pack = mClass.getPackage().toString().split(" ")[1];
        final String packageDir = pack.replaceAll("[.]", "/");
        final URL url = mClass.getProtectionDomain().getCodeSource().getLocation();
        try {
            if (new File(url.getFile()).isFile()) {
                printAllClass(packageDir, url);
            } else {
                printAllClass(0, packageDir, mClass);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printAllClass(final String pack, final URL url) throws IOException {
        final ZipInputStream zip = new ZipInputStream(url.openStream());
        while(true) {
            final ZipEntry e = zip.getNextEntry();
            if (e == null)
                break;
            final String name = e.getName();
            if (name.startsWith(pack) && name.endsWith(".class") && !name.contains("$") && !name.contains("colladaConverter")) {
                pyInterpreter.exec("from " + name.substring(0, name.lastIndexOf('/')).replaceAll("[/]", ".") + " import " + name.substring(name.lastIndexOf('/') + 1, name.length() - 6));
            }
        }
    }

    private void printAllClass(final int level, final String pack, final Class mClass) throws IOException {
        final ClassLoader cl = mClass.getClassLoader();
        final URL upackage = cl.getResource(pack);
        final InputStream in = (InputStream) upackage.getContent();
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("$") || line.contains("colladaConverter") || line.endsWith("glsl") || line.endsWith("png")  || line.endsWith("fnt"))
                continue;
            if (line.endsWith(".class")) {
                pyInterpreter.exec("from " + pack.replaceAll("[/]", ".") + " import " + line.substring(0, line.length() - 6));
                continue;
            }
            try {
                printAllClass(level + 1,pack + "/" + line, mClass);
            } catch (NullPointerException e) {}
        }
    }

    void execCommand(final String command) {
        try {
            final PyObject result = eval(command);
            if (!result.getType().fastGetName().equals("NoneType")) {
                System.out.println(result);
            }
        } catch (final Exception e) {
            try {
                exec(command);
            } catch (final Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}
