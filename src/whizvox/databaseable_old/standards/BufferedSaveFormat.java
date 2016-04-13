package whizvox.databaseable_old.standards;

import whizvox.databaseable_old.*;

import java.io.*;
import java.nio.CharBuffer;

public class BufferedSaveFormat extends SaveFormat {

    public static final int BUFFER_SIZE_DEFAULT = 512;

    private int bufferSize;

    /**
     * If you're expecting rows that are longer than {@link #BUFFER_SIZE_DEFAULT}, then you'll need to change this.
     * @param bufferSize The amount of space allocated when writing and reading.
     */
    public BufferedSaveFormat(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public BufferedSaveFormat() {
        this(BUFFER_SIZE_DEFAULT);
    }

    @SuppressWarnings("unchecked")
    @Override public <T extends Row> void write(OutputStream out, Database<T> database) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        CharBuffer charBuffer = CharBuffer.allocate(bufferSize);
        for (int i = 0; i < database.size(); i++) {
            Row row = database.getFromIndex(i);
            for (int j = 0; j < database.columnCount; j++) {
                Codec codec = database.getCodec(j);
                Object obj = row.getObject(j);
                if (obj == null) {
                    charBuffer.append(getNullString());
                } else if (codec.requiresSanitation()) {
                    charBuffer.append(sanitize(codec.write(obj)));
                } else {
                    charBuffer.append(codec.write(obj));
                }
                if (j < database.columnCount - 1) {
                    charBuffer.append(getObjectSeparator());
                }
            }
            charBuffer.append(getRowSeparator());
            int pos = charBuffer.position();
            charBuffer.rewind();
            writer.append(charBuffer.subSequence(0, pos));
            charBuffer.clear();
        }
        writer.close();
    }

    @Override public <T extends Row> void read(InputStream in, Database<T> database) throws IOException {
        CharBuffer charBuffer = CharBuffer.allocate(bufferSize);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int read, index = 0;
        while ((read = reader.read()) != -1) {
            char c = (char) read;
            if (c == getRowSeparator()) {
                charBuffer.rewind();
                String s = charBuffer.subSequence(0, index).toString();
                String[] objsStr = StringUtils.splitAtChar(s, getObjectSeparator(), database.columnCount);
                T row;
                try {
                    row = database.rowClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                row.allocate(database.columnCount);
                for (int i = 0; i < objsStr.length; i++) {
                    String objStr = objsStr[i];
                    Codec codec = database.getCodec(i);
                    if (getNullString().equals(objStr)) {
                        row.set(i, null);
                    } else if (codec.requiresSanitation()) {
                        row.set(i, codec.read(revertSanitizing(objStr)));
                    } else {
                        row.set(i, codec.read(objStr));
                    }
                }
                database.add(row);
                charBuffer.clear();
                index = 0;
                continue;
            }
            charBuffer.put(c);
            index++;
        }
        reader.close();
    }

}
