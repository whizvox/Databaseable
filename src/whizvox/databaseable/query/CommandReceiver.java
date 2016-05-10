package whizvox.databaseable.query;

import whizvox.databaseable.Database;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class CommandReceiver implements Runnable {

    private Map<String, Database> cache = new HashMap<>();
    private Map<String, CommandController> cmdControllers = new HashMap<>();
    private Map<String, StatementController> statementControllers = new HashMap<>();
    private InputStream in = null;
    private PrintStream out = null;

    public CommandReceiver in(InputStream in) {
        this.in = in;
        return this;
    }

    public CommandReceiver sysin() {
        return in(System.in);
    }

    public CommandReceiver out(PrintStream out) {
        this.out = out;
        return this;
    }

    public CommandReceiver sysout() {
        return out(System.out);
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

    public CommandReceiver defaultStatements() {
        for (Field field : StatementController.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                try {
                    statementControllers.put(field.getName().toLowerCase(), (StatementController) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new Error("Illegal tampering detected! Abort! Abort!");
                }
            }
        }
        return this;
    }

    public void addCommand(String name, CommandController controller) {
        cmdControllers.put(name, controller);
    }

    public void addStatement(String name, StatementController controller) {
        statementControllers.put(name, controller);
    }

    public StatementController getStatementController(String name) {
        return statementControllers.get(name);
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

    public void clearCache() {
        cache.clear();
    }

    public ObjectOutput executeCommand(Command cmd) {
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

    public void exception(PrintStream out, Exception e) {
        out.println();
        out.println(e.getMessage());
        out.println();
    }

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
                String cmdName = userIn.substring(0, userIn.indexOf(' '));
                List<Statement> statements = Statement.getStatements(userIn);
                Command cmd = new Command(cmdName, statements);
                executeCommand(cmd);
            } catch (Exception e) {
                exception(out, e);
            }
        }
    }

    // TODO: Add working example of this class

}
