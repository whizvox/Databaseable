package whizvox.databaseable.query;

import java.util.ArrayList;
import java.util.List;

public class Statement {

    public final String name;
    private String[] args;

    public Statement(String name, String... args) {
        this.name = name;
        this.args = args;
    }

    public Statement(String name, List<String> list) {
        this(name, list.toArray(new String[list.size()]));
    }

    public Object get(int index) {
        return args[index];
    }

    public int getNumberArgs() {
        return args.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        for (String arg : args) {
            sb.append('(').append(arg).append(')').append(' ');
        }
        return sb.toString();
    }

    // warning: ugly parsing ahead :)
    public static List<Statement> getStatements(String cmd) {
        List<Statement> list = new ArrayList<>();
        boolean inString = false, backslash = false, wasString = false;
        int openParen = 0, lastSpace = 0, lastComma = 0;

        String currentStatementName = null;
        List<String> currentStatementArgs = new ArrayList<>();

        for (int i = cmd.indexOf(' '); i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (backslash) {
                backslash = false;
            } else if (inString) {
                if (c == '"') {
                    inString = false;
                    wasString = true;
                } else if (c == '\\') {
                    backslash = true;
                }
            } else {
                switch (c) {
                    case '"':
                        inString = true;
                        break;
                    case '(':
                        openParen++;
                        if (openParen == 1) {
                            lastComma = i;
                            currentStatementName = cmd.substring(lastSpace + 1, i);
                            lastSpace = i;
                        }
                        break;
                    case ')':
                        openParen--;
                        if (openParen == 0) {
                            String arg = cmd.substring(Math.min(lastComma, lastSpace) + 1, Math.max(lastSpace, i)).trim();
                            if (wasString) {
                                arg = arg.substring(1, arg.length() - 1).replace("\\\"", "\"");
                            }
                            if (currentStatementArgs.size() > 0 || (wasString && currentStatementArgs.size() == 0) || !arg.equals("")) {
                                wasString = false;
                                currentStatementArgs.add(arg);
                            }
                            list.add(new Statement(currentStatementName, currentStatementArgs));
                            currentStatementArgs.clear();
                            currentStatementName = null;
                        }
                        break;
                    case ' ':
                        lastSpace = i;
                        break;
                    case ',':
                        String arg = cmd.substring(Math.min(lastComma, lastSpace) + 1, Math.max(lastSpace, i)).trim();
                        if (wasString) {
                            arg = arg.substring(1, arg.length() - 1).replace("\\\"", "\"");
                            wasString = false;
                        }
                        currentStatementArgs.add(arg);
                        lastComma = i;
                        break;
                    default:
                }
            }
        }
        return list;
    }

}
