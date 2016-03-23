package whizvox.databaseable;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class DefaultSaveFormats {

    private DefaultSaveFormats() {}

    public static final SaveFormat FORMAT_STANDARD = new SaveFormat() {
        @Override
        public void write(OutputStream out, Database database) throws IOException {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            CharBuffer charBuffer = CharBuffer.allocate(1024);
            char[] buf = new char[1024];
            int size;
            for (int i = 0; i < database.size(); i++) {
                Object[] objs = database.getRow(i).getObjects();
                for (int j = 0; j < objs.length; j++) {
                    charBuffer.append('"');
                    database.getCodec(j).write(charBuffer, objs[j]);
                    charBuffer.append('"');
                    if (j < objs.length - 1) {
                        charBuffer.append(',');
                    }
                    charBuffer.flip();
                    charBuffer.rewind();
                    size = charBuffer.remaining();
                    charBuffer.get(buf, 0, size);
                    writer.write(buf, 0, size);
                    charBuffer.clear();
                }
                writer.newLine();
            }
            writer.flush();
        }
        @Override
        public <T extends Row> void read(InputStream in, Database<T> database) throws IOException {
            /*BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            CharBuffer charBuffer = CharBuffer.allocate(1024);
            *//*char[] buf = new char[1024];
            int size;*//*
            List<String> list = new ArrayList<>(database.columnCount);
            Object[] objs = new Object[database.columnCount];
            reader.lines().forEach(s -> {
                DatabaseableUtil.grabAllStrings(list, s, 0, s.length());
                for (int i = 0; i < objs.length; i++) {
                    charBuffer.append(list.get(i));
                    charBuffer.flip();
                    charBuffer.rewind();
                    objs[i] = database.getCodec(i).read(charBuffer);
                    charBuffer.clear();
                }
                T obj =
                database.addRow();
            });*/
        }
    };

}
