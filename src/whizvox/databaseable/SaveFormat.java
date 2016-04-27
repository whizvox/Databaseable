package whizvox.databaseable;

import whizvox.databaseable.codec.DataCodec;
import whizvox.databaseable.io.ByteContainer;
import whizvox.databaseable.io.ByteWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class SaveFormat {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private byte[] buffer;
    public final int bufferSize;

    public SaveFormat(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = null;
    }

    public SaveFormat() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public SaveFormat allocate() {
        buffer = new byte[bufferSize];
        return this;
    }

    protected final void checkInit() {
        if (buffer == null) {
            allocate();
        }
    }

    public void save(Database database, OutputStream out) throws IOException {
        checkInit();

        ByteWriter writer = new ByteWriter(buffer);
        CODEC_INT.write(writer, database.getVersion());
        CODEC_INT.write(writer, database.getRowCount());
        CODEC_ARRAY_STR16.write(writer, database.getNames());
        writer.writeTo(out);

        for (int i = 0; i < database.getRowCount(); i++) {
            Row row = database.get(i);
            for (int j = 0; j < database.getColumnCount(); j++) {
                // Generics warning here, just ignore it
                database.getCodecs()[j].write(writer, row.get(j));
            }
            writer.writeTo(out);
        }
    }

    public <T extends Row> void load(Database<T> database, InputStream in) throws IOException {
        checkInit();

        // TODO: Working on loading
    }

}
