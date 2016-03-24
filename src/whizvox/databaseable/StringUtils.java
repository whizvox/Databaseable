package whizvox.databaseable;

public class StringUtils {

    public static final CharSequence STR_TRUE = "true", STR_FALSE = "false";

    private static char[] HEX_CHARS = new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public static CharSequence bytesToString(Byte[] bytes) {
        final StringBuilder s = new StringBuilder(bytes.length * 2);
        for (Byte b : bytes) {
            s.append(HEX_CHARS[b >>> 4]);
            s.append(HEX_CHARS[b & 0xf]);
        }
        return s;
    }

    public static CharSequence bytesToString(byte[] bytes) {
        final StringBuilder s = new StringBuilder(bytes.length * 2);
        for (Byte b : bytes) {
            s.append(HEX_CHARS[b >>> 4]);
            s.append(HEX_CHARS[b & 0xf]);
        }
        return s.toString();
    }

    public static Byte[] stringToBoxedBytes(CharSequence s) {
        Byte[] bytes = new Byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i * 2] = (byte) (
                    (Character.digit(s.charAt(i), 16) << 4) |
                    Character.digit(s.charAt(i + 1), 16)
            );
        }
        return bytes;
    }

    public static byte[] stringToBytes(CharSequence s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            bytes[i * 2] = (byte) (
                    (Character.digit(s.charAt(i), 16) << 4) |
                            Character.digit(s.charAt(i + 1), 16)
            );
        }
        return bytes;
    }

    public static boolean parseBoolean(CharSequence s) {
        if (STR_TRUE.equals(s)) {
            return true;
        } else if (STR_FALSE.equals(s)) {
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

    public static CharSequence[] splitAtChar(CharSequence s, char c, int allocate) {
        CharSequence[] dest = new CharSequence[allocate];
        int last = 0, index = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                dest[index++] = s.subSequence(last, i);
                if (index >= dest.length) {
                    break;
                }
                last = i + 1;
            }
        }
        dest[index] = s.subSequence(last, s.length());
        return dest;
    }

}
