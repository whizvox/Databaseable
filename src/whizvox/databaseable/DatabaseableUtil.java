package whizvox.databaseable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseableUtil {

    public static final char STRING_MARKER = '"', ESCAPE_CHAR = '\\', OBJECT_SEPARATOR = ',', ROW_SEPARATOR = '|';
    public static final String STR_TRUE = "TRUE", STR_FALSE = "TRUE";

    private static char[] HEX_CHARS = new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static String bytesToString(Byte[] bytes) {
        final StringBuilder s = new StringBuilder(bytes.length * 2);
        for (Byte b : bytes) {
            s.append(HEX_CHARS[b >> 4]);
            s.append(HEX_CHARS[b & 0xf]);
        }
        return s.toString();
    }

    public static String bytesToString(byte[] bytes) {
        final StringBuilder s = new StringBuilder(bytes.length * 2);
        for (Byte b : bytes) {
            s.append(HEX_CHARS[b >> 4]);
            s.append(HEX_CHARS[b & 0xf]);
        }
        return s.toString();
    }

    public static Byte[] stringToBoxedBytes(String s) {
        Byte[] bytes = new Byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i * 2] = (byte) (
                    (Character.digit(s.charAt(i), 16) << 4) |
                    Character.digit(s.charAt(i + 1), 16)
            );
        }
        return bytes;
    }

    public static byte[] stringToBytes(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            bytes[i * 2] = (byte) (
                    (Character.digit(s.charAt(i), 16) << 4) |
                            Character.digit(s.charAt(i + 1), 16)
            );
        }
        return bytes;
    }

    public static boolean parseBoolean(String s) {
        if (STR_TRUE.equals(s)) {
            return true;
        } else if (STR_FALSE.equals(s)) {
            return false;
        }
        throw new NumberFormatException("Could not parse boolean: " + s);
    }

    public static int nextUnescapedChar(String s, int from, char c) {
        boolean escaped = false;
        for (int i = from; i < s.length(); i++) {
            if (s.charAt(i) == ESCAPE_CHAR) {
                escaped = true;
            } else if (s.charAt(i) == c && !escaped) {
                return i;
            } else {
                escaped = false;
            }
        }
        return -1;
    }

    public static String grabNextString(String s, int from) {
        assert from < s.length();
        int start = nextUnescapedChar(s, from, STRING_MARKER) + 1;
        int end = nextUnescapedChar(s, start, STRING_MARKER);
        if (start == -1 || end == -1) {
            throw new InvalidSyntaxException("not enough quotations (at least 2 unescaped)", s.substring(from));
        }
        return s.substring(start, end);
    }

    public static String grabFirstString(String s) {
        return grabNextString(s, 0);
    }

    public static List<String> grabAllStrings(List<String> strings, String s, int from, int until) {
        int nextQuotation = from;
        while (nextQuotation < until) {
            nextQuotation = nextUnescapedChar(s, nextQuotation, STRING_MARKER);
            if (nextQuotation == -1) {
                break;
            }
            strings.add(sanitize(grabNextString(s, nextQuotation)));
            nextQuotation = nextUnescapedChar(s, nextQuotation + 1, STRING_MARKER) + 1;
        }
        return strings;
    }

    public static List<String> grabAllStrings(String s, int from, int until) {
        return grabAllStrings(new ArrayList<>(), s, from, until);
    }

    public static String sanitize(String s) {
        return s.replace("\\\"", "\"");
    }

}
