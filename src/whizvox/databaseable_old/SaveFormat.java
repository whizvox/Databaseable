package whizvox.databaseable_old;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SaveFormat {

    public static final char
            SEPARATOR_OBJ_DEFAULT = ';',
            SEPARATOR_ROW_DEFAULT = '\n';
    public static final String
            SANITIZED_OBJ_DEFAULT = "{O}",
            SANITIZED_ROW_DEFAULT = "{R}",
            NULL_STRING_DEFAULT = "%NULL%";

    private char
            objSeparator = SEPARATOR_OBJ_DEFAULT,
            rowSeparator = SEPARATOR_ROW_DEFAULT;
    private String
            objSeparatorReplacer = SANITIZED_OBJ_DEFAULT,
            rowSeparatorReplacer = SANITIZED_ROW_DEFAULT,
            nullString           = NULL_STRING_DEFAULT;

    public SaveFormat() {

    }

    public SaveFormat setObjectSeparator(char objectSeparator) {
        assert objSeparatorReplacer.indexOf(objectSeparator) == -1;
        this.objSeparator = objectSeparator;
        return this;
    }

    public SaveFormat setRowSeparator(char rowSeparator) {
        assert rowSeparatorReplacer.indexOf(rowSeparator) == -1;
        this.rowSeparator = rowSeparator;
        return this;
    }

    public SaveFormat setObjectSeparatorReplacer(String s) {
        objSeparatorReplacer = s;
        return this;
    }

    public SaveFormat setRowSeparatorReplacer(String s) {
        rowSeparatorReplacer = s;
        return this;
    }

    public SaveFormat setNullString(String s) {
        nullString = s;
        return this;
    }

    public final char getObjectSeparator() {
        return objSeparator;
    }

    public final char getRowSeparator() {
        return rowSeparator;
    }

    public String getObjectSeparatorReplacer() {
        return objSeparatorReplacer;
    }

    public String getRowSeparatorReplacer() {
        return rowSeparatorReplacer;
    }

    public String getNullString() {
        return nullString;
    }

    public String sanitize(String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == objSeparator) {
                sb.append(objSeparatorReplacer);
            } else if (c == rowSeparator) {
                sb.append(rowSeparatorReplacer);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String revertSanitizing(String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        int len = Math.max(objSeparatorReplacer.length(), rowSeparatorReplacer.length());
        for (int i = 0; i < s.length() - len; i++) {
            String subSeq = s.subSequence(i, i + len).toString();
            if (subSeq.startsWith(objSeparatorReplacer)) {
                sb.append(objSeparator);
                i += objSeparatorReplacer.length() - 1;
            } else if (subSeq.startsWith(rowSeparatorReplacer)) {
                sb.append(rowSeparator);
                i += rowSeparatorReplacer.length() - 1;
            } else {
                sb.append(s.charAt(i));
            }
            if (i == s.length() - len - 1) {
                sb.append(s.subSequence(s.length() - len, s.length()));
            }
        }
        return sb.toString();
    }

    public boolean isNullString(String s) {
        return nullString.equals(s);
    }

    public abstract <T extends Row> void write(OutputStream out, Database<T> database) throws IOException;

    public abstract <T extends Row> void read(InputStream in, Database<T> database) throws IOException;

}
