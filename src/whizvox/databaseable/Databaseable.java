package whizvox.databaseable;

public class Databaseable {

    public static final int
            MAX_STRING_SIZE = Short.MAX_VALUE,
            MAX_DATA_SIZE = Integer.MAX_VALUE / 2;

    public static final String VERSION = "${version}";

    public static final boolean parseBool(String s) {
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t") || s.equals("1")) {
            return true;
        } else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f") || s.equals("0")) {
            return false;
        }
        throw new NumberFormatException("Invalid boolean: " + s);
    }

}
