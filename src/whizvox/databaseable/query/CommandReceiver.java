package whizvox.databaseable.query;

import whizvox.databaseable.Database;
import whizvox.databaseable.Row;
import whizvox.databaseable.codec.DataCodec;
import whizvox.databaseable.codec.StandardCodecs;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class CommandReceiver implements Runnable {

    private Map<String, Database> cache = new HashMap<>();
    private Map<String, CommandController> cmdControllers = new HashMap<>();
    private Map<String, ArgumentController> argControllers = new HashMap<>();
    private Map<String, DataCodec> codecs = new HashMap<>();
    private Class<? extends Row> workingRow = null;
    private InputStream in = null;
    private OutputStream out = null;

    public CommandReceiver in(InputStream in) {
        this.in = in;
        return this;
    }

    public CommandReceiver sysin() {
        return in(System.in);
    }

    protected final InputStream getIn() {
        return in;
    }

    public CommandReceiver out(OutputStream out) {
        this.out = out;
        return this;
    }

    public CommandReceiver sysout() {
        return out(System.out);
    }

    protected final OutputStream getOut() {
        return out;
    }

    public CommandReceiver defaultCommands() {
        for (Field field : CommandController.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    cmdControllers.put(field.getName().toLowerCase(), (CommandController) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new Error("Illegal tampering detected! Abort! Abort!");
                }
            }
        }
        return this;
    }

    public CommandReceiver defaultArguments() {
        for (Field field : ArgumentController.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    argControllers.put(field.getName().toLowerCase(), (ArgumentController) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new Error("Illegal tampering detected! Abort! Abort!");
                }
            }
        }
        return this;
    }

    public CommandReceiver defaultCodecs() {
        for (Field field : StandardCodecs.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("CODEC_")) {
                String name = field.getName().toLowerCase().substring(6);
                if (name.startsWith("array_")) {
                    name = name.substring(6) + "[]";
                } else if (name.startsWith("list_")) {
                    name = name.substring(5) + "<>";
                }
                try {
                    codecs.put(name, (DataCodec) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new Error("Illegal tampering detected! Abort! Abort!");
                }
            }
        }
        return this;
    }

    public CommandReceiver workingRow(Class<? extends Row> workingRow) {
        this.workingRow = workingRow;
        return this;
    }

    public DataCodec getCodec(String name) {
        return codecs.get(name);
    }

    public Class<? extends Row> getWorkingRow() {
        return workingRow;
    }

    public void addCommand(String name, CommandController controller) {
        cmdControllers.put(name, controller);
    }

    public void addArgument(String name, ArgumentController controller) {
        argControllers.put(name, controller);
    }

    public ArgumentController getArgumentController(String name) {
        return argControllers.get(name);
    }

    public boolean databaseExists(String name) {
        return cache.containsKey(name);
    }

    public void cacheNew(String name, Database db) throws IOException {
        db.load();
        db.save();
        cache.put(name, db);
    }

    public Database getCached(String name) {
        return cache.get(name);
    }

    // TODO: Think of better way to do this
    public void remove(String name) {
        Database db = cache.get(name);
        if (db == null) {
            throw new NullPointerException("database");
        }
        File file = new File(name + ".db");
        if (file.exists()) {
            file.delete();
        }
        cache.remove(name);
    }

    public Iterable<Map.Entry<String, Database>> getDatabases() {
        return cache.entrySet();
    }

    public void clearCache() {
        cache.clear();
    }

    protected ObjectOutput executeCommand(Command cmd) {
        CommandController cmdController;
        if (cmdControllers.containsKey(cmd.name)) {
            cmdController = cmdControllers.get(cmd.name);
        } else {
            throw new IllegalArgumentException("Invalid command: " + cmd.name);
        }
        ObjectOutput out = new ObjectOutput();
        cmdController.control(this, out, cmd);
        return out;
    }

    protected abstract void exception(OutputStream out, Throwable t);

    protected abstract void onExecute(Command cmd, ObjectOutput out);

    @Override
    public final void run() {
        if (in == null) {
            sysin();
        }
        if (out == null) {
            sysout();
        }
        Scanner scanner = new Scanner(in);
        String userIn;
        while (!(userIn = scanner.nextLine()).equals("quit")) {
            try {
                int indexOf = userIn.indexOf(' ');
                String cmdName;
                List<Argument> arguments;
                Command cmd;
                if (indexOf == -1) {
                    cmdName = userIn;
                    cmd = new Command(cmdName);
                } else {
                    cmdName = userIn.substring(0, indexOf);
                    arguments = Argument.getArguments(userIn);
                    cmd = new Command(cmdName, arguments);
                }
                onExecute(cmd, executeCommand(cmd));
            } catch (Throwable t) {
                exception(out, t);
            }
        }
    }

}
