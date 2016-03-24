package whizvox.databaseable.standards;

import whizvox.databaseable.*;

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

    @Override
    @SuppressWarnings("unchecked")
    public void write(OutputStream out, Database<? extends Row> database) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        CharBuffer buf = CharBuffer.allocate(bufferSize);
        for (int i = 0; i < database.size(); i++) {
            Row row = database.getFromIndex(i);
            for (int j = 0; j < database.columnCount; j++) {
                Codec codec = database.getCodec(j);
                if (codec.requiresSanitation()) {
                    buf.append(sanitize(codec.write(row.getObject(j))));
                } else {
                    buf.append(codec.write(row.getObject(j)));
                }
                if (j < database.columnCount - 1) {
                    buf.append(getObjectSeparator());
                }
            }
            buf.append(getRowSeparator());
            int pos = buf.position();
            buf.rewind();
            writer.append(buf.subSequence(0, pos));
            buf.clear();
        }
        writer.close();
    }

    @Override
    public <T extends Row> void read(InputStream in, Database<T> database) throws IOException {
        CharBuffer buf = CharBuffer.allocate(bufferSize);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int read, index = 0;
        while ((read = reader.read()) != -1) {
            char c = (char) read;
            if (c == getRowSeparator()) {
                buf.rewind();
                CharSequence s = buf.subSequence(0, index);
                CharSequence[] objsStr = StringUtils.splitAtChar(s, getObjectSeparator(), database.columnCount);
                T row;
                try {
                    row = database.rowClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                row.setObjects(new Object[database.columnCount]);
                for (int i = 0; i < objsStr.length; i++) {
                    Codec codec = database.getCodec(i);
                    if (codec.requiresSanitation()) {
                        row.set(i, codec.read(revertSanitizing(objsStr[i])));
                    } else {
                        row.set(i, codec.read(objsStr[i]));
                    }
                }
                database.add(row);
                buf.clear();
                index = 0;
                continue;
            }
            buf.put(c);
            index++;
        }
        reader.close();
    }

}
