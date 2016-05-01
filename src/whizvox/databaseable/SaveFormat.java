package whizvox.databaseable;

import whizvox.databaseable.io.ByteReader;
import whizvox.databaseable.io.ByteWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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
        Date lastSaved = new Date();
        database.setLastSaved(lastSaved);
        CODEC_DATE.write(writer, lastSaved);
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

    public <T extends Row> void load(Database database, Class<T> rowClass, InputStream in) throws IOException {
        checkInit();

        ByteReader reader = new ByteReader(buffer);
        reader.readFromStream(in);
        int version = CODEC_INT.read(reader);
        database.checkVersion(version);
        int rowCount = CODEC_INT.read(reader);
        Date lastSaved = CODEC_DATE.read(reader);
        database.setLastSaved(lastSaved);

        try {
            for (int i = 0; i < rowCount; i++) {
                T row = rowClass.newInstance();
                for (int j = 0; j < database.getColumnCount(); j++) {
                    row.setObject(j, database.getCodecs()[j].read(reader));
                }
                database.add(row);
                reader.readFromStream(in);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new InvalidDataException(e);
        }
    }

}
