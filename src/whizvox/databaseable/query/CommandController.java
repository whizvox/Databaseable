package whizvox.databaseable.query;

import whizvox.databaseable.Database;
import whizvox.databaseable.Row;
import whizvox.databaseable.codec.DataCodec;

import java.io.File;
import java.io.IOException;

public interface CommandController {

    void control(CommandReceiver receiver, ObjectOutput<Object> out, Command cmd);

    static void checkDatabaseExist(CommandReceiver receiver, String name, boolean alreadyExists) {
        if (receiver.databaseExists(name)) {
            if (alreadyExists) {
                throw new IllegalArgumentException("Database already exists: " + name);
            }
        } else {
            if (!alreadyExists) {
                throw new IllegalArgumentException("Database does not exist: " + name);
            }
        }
    }

    CommandController LOAD = (receiver, out, cmd) -> {
        String dbName = cmd.arguments[0].name;
        checkDatabaseExist(receiver, dbName, true);
        File file = new File(dbName + ".db");
        if (!file.exists()) {
            try {
                file.createNewFile();
                out.add("Database created: " + file.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int size = cmd.arguments[0].getNumberParams();
        Database<? extends Row> db = new Database<>(size, receiver.getWorkingRow())
                .file(file);
        String[] names = new String[size];
        DataCodec[] codecs = new DataCodec[size];
        for (int i = 0; i < names.length; i++) {
            String s = cmd.arguments[0].get(i);
            int indexOf = s.indexOf(' ');
            if (indexOf == -1) {
                throw new IllegalArgumentException("Invalid parameter. Requires a type and a name: " + s);
            }
            DataCodec codec = receiver.getCodec(s.substring(0, indexOf).trim());
            if (codec == null) {
                throw new IllegalArgumentException("Invalid codec: " + s.substring(0, indexOf));
            }
            names[i] = s.substring(indexOf + 1).trim();
            codecs[i] = codec;
        }
        db.names(names);
        db.codecs(codecs);
        try {
            receiver.cacheNew(dbName, db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.add("Database loaded into memory: " + file.getAbsolutePath());
    };

    CommandController REM = (receiver, out, cmd) -> {
        String dbName = cmd.arguments[0].name;
        checkDatabaseExist(receiver, dbName, false);
        receiver.remove(dbName);
        out.add("Removed " + dbName + ".db");
    };

    CommandController INFO = (receiver, out, cmd) -> {
        String dbName = cmd.arguments[0].name;
        checkDatabaseExist(receiver, dbName, false);
        Database db = receiver.getCached(dbName);
        out.add("Rows: " + db.getRowCount());
        out.add("Row Type: " + db.getRowClass().getName());
        out.add("Columns: " + db.getColumnCount());
        for (int i = 0; i < db.getColumnCount(); i++) {
            // TODO: Get a human-readable type
            out.add(i + ": " + db.getName(i) + " (" + db.getCodec(i).getClass().getName()+ ")");
        }
        out.add("First 10 Rows:");
        for (int i = 0; i < Math.min(10, db.getRowCount()); i++) {
            out.add(i + ": " + db.get(i));
        }
    };

    CommandController GET = (receiver, out, cmd) -> {
        String dbName = cmd.arguments[0].name;
        checkDatabaseExist(receiver, dbName, false);
        Database db = receiver.getCached(dbName);
        ObjectOutput<Row> result = new ObjectOutput<>();
        result.putAll(db);
        for (int i = 1; i < cmd.arguments.length; i++) {
            Argument arg = cmd.arguments[i];
            ArgumentController controller = receiver.getArgumentController(arg.name);
            if (controller == null) {
                throw new IllegalArgumentException("Invalid argument: " + arg.name);
            }
            controller.control(result, db, controller.parseParams(arg.getAll()));
        }
        result.forEach(out::add);
    };

    CommandController LIST = (receiver, out, cmd) -> {
        receiver.getDatabases().forEach(e -> out.add(e.getKey() + ": " + e.getValue()));
    };

}
