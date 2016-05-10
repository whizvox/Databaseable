package whizvox.databaseable.query;

import whizvox.databaseable.Database;

public interface CommandController {

    void control(CommandReceiver receiver, ObjectOutput out, Command cmd);

    CommandController CREATE = (receiver, out, cmd) -> {
        String dbName = cmd.statements[0].name;
        if (receiver.databaseExists(dbName)) {
            throw new IllegalArgumentException("Database already exists: " + dbName);
        }
    };

    // TODO: Add more controllers!

}
