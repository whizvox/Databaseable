package whizvox.databaseable.query;

import java.util.List;

public class Command {

    public final String name;
    public final Argument[] arguments;

    public Command(String name, List<Argument> args) {
        this.name = name;
        this.arguments = new Argument[args.size()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = args.get(i);
        }
    }

    public Command(String name) {
        this.name = name;
        arguments = null;
    }

}
