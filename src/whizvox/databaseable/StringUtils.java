package whizvox.databaseable;

public class StringUtils {

    public static final String STR_TRUE = "true", STR_TRUE_BIT = "1", STR_FALSE = "false", STR_FALSE_BIT = "0";

    private static char[] HEX_CHARS = new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public static String bytesToHexString(byte[] bytes) {
        final StringBuilder s = new StringBuilder(bytes.length * 2);
        for (Byte b : bytes) {
            s.append(HEX_CHARS[b >>> 4]);
            s.append(HEX_CHARS[b & 0xf]);
        }
        return s.toString();
    }

    public static byte[] hexStringToBytes(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            bytes[i * 2] = (byte) (
                    (Character.digit(s.charAt(i), 16) << 4) |
                    Character.digit(s.charAt(i + 1), 16)
            );
        }
        return bytes;
    }

    public static String booleanToString(boolean b) {
        return b ? STR_TRUE : STR_FALSE;
    }

    public static String booleanToBitString(boolean b) {
        return b ? STR_TRUE_BIT : STR_FALSE_BIT;
    }

    public static boolean parseBoolean(String s) {
        if (STR_TRUE.equals(s) || STR_TRUE_BIT.equals(s)) {
            return true;
        } else if (STR_FALSE.equals(s) || STR_FALSE_BIT.equals(s)) {
            return false;
        }
        throw new NumberFormatException("Could not parse boolean: " + s);
    }

    public static CharSequence sanitize(CharSequence s, char replacing, CharSequence replacement) {
        final StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == replacing) {
                sb.append(replacement);
            } else {
                sb.append(c);
            }
        }
        return sb;
    }

    public static CharSequence revertSanitizing(CharSequence s, CharSequence replacing, char replacement) {
        final StringBuilder sb = new StringBuilder(s.length());
        int len = replacing.length();
        for (int i = 0; i < s.length() - len; i++) {
            if (s.subSequence(i, len).equals(replacing)) {
                sb.append(replacement);
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb;
    }

    public static String[] splitAtChar(String s, char c, int allocate) {
        String[] dest = new String[allocate];
        int last = 0, index = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                dest[index++] = s.substring(last, i);
                if (index >= dest.length) {
                    break;
                }
                last = i + 1;
            }
        }
        dest[index] = s.substring(last);
        return dest;
    }

}
