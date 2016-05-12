package whizvox.databaseable.query;

import java.util.ArrayList;
import java.util.List;

public class Argument {

    public final String name;
    private String[] params;

    public Argument(String name, String... params) {
        this.name = name;
        this.params = params;
    }

    public Argument(String name, List<String> paramsList) {
        this(name, paramsList.toArray(new String[paramsList.size()]));
    }

    public String get(int index) {
        return params[index];
    }

    public String[] getAll() {
        return params;
    }

    public int getNumberParams() {
        return params.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ");
        for (String arg : params) {
            sb.append('(').append(arg).append(')').append(' ');
        }
        return sb.toString();
    }

    // warning: ugly parsing ahead :)
    public static List<Argument> getArguments(String cmd) {
        List<Argument> list = new ArrayList<>();
        boolean inString = false, backslash = false, wasString = false;
        int openParen = 0, lastSpace = 0, lastComma = 0;

        String currentArgName = null;
        List<String> currentArgParams = new ArrayList<>();

        int indexOf = cmd.indexOf(' ');
        if (indexOf == -1) {
            return null;
        }
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
                            currentArgName = cmd.substring(lastSpace + 1, i);
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
                            if (currentArgParams.size() > 0 || (wasString && currentArgParams.size() == 0) || !arg.equals("")) {
                                wasString = false;
                                currentArgParams.add(arg);
                            }
                            list.add(new Argument(currentArgName, currentArgParams));
                            currentArgParams.clear();
                            currentArgName = null;
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
                        currentArgParams.add(arg);
                        lastComma = i;
                        break;
                    default:
                }
            }
        }
        return list;
    }

}
