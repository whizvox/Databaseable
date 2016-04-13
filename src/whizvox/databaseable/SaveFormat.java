package whizvox.databaseable;

import whizvox.databaseable.codec.DbCodec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class SaveFormat {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private byte[] buf;
    private ByteBuffer buffer;
    public final int bufferSize;

    public SaveFormat(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public SaveFormat() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public SaveFormat allocate() {
        buf = new byte[bufferSize];
        buffer = ByteBuffer.wrap(buf);
        return this;
    }

    protected final void checkInit() {
        if (buf == null) {
            allocate();
        }
    }

    public void save(Database database, OutputStream out) throws IOException {
        checkInit();

        CODEC_INT.write(buffer, database.getVersion());
        CODEC_INT.write(buffer, database.getColumnCount());
        CODEC_DATE.write(buffer, new Date());
        CODEC_ARRAY_STRING_16BIT.write(buffer, database.getNames());

        out.write(buf, 0, buffer.position());

        for (int i = 0; i < database.getRowCount(); i++) {
            buffer.flip();
            Row row = database.get(i);
            for (int j = 0; j < database.getColumnCount(); j++) {
                database.getCodecs()[j].write(buffer, row);
            }
            out.write(buf, 0, buffer.position());
        }
    }

    public <T extends Row> void load(Database<T> database, InputStream in) throws IOException {
        checkInit();

        in.read(buf, 0, buf.length);
        int version = CODEC_INT.read(buffer);
        int columnCount = CODEC_INT.read(buffer);
        Date lastSaved = CODEC_DATE.read(buffer);
        String[] names = CODEC_ARRAY_STRING_16BIT.read(buffer);

        database.checkVersion(version);
        database.setColumnCount(columnCount);
        database.setLastSaved(lastSaved);
        database.setColumnNames(names);
        try {
            while (rotate(in) != -1) {
                T row = database.getRowClass().newInstance();
                for (int i = 0; i < database.getColumnCount(); i++) {
                    DbCodec codec = database.getCodecs()[i];
                    row.setObject(i, codec.read(buffer));
                }
                database.add(row);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new InvalidDataException(e);
        }
    }

    private int rotate(InputStream in) throws IOException {
        System.arraycopy(buf, buffer.position(), buf, 0, buffer.remaining());
        return in.read(buf, buffer.position(), buffer.remaining());
    }

}
