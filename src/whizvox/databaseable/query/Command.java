package whizvox.databaseable.query;

import java.util.List;

public class Command {

    public final String name;
    public final Statement[] statements;

    public Command(String name, List<Statement> list) {
        this.name = name;
        this.statements = new Statement[list.size()];
        for (int i = 0; i < statements.length; i++) {
            statements[i] = list.get(i);
        }
    }

}
