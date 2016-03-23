package whizvox.databaseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Database<T extends Row> {

    public final int columnCount;
    private final Codec[] codecs;
    private final SaveFormat saveFormat;
    private final List<T> rows;

    public Database(int columnCount, SaveFormat saveFormat, int initialCapacity) {
        this.columnCount = columnCount;
        codecs = new Codec[columnCount];
        this.saveFormat = saveFormat;
        rows = new ArrayList<>(initialCapacity);
    }

    public Database(int columnCount, int initialCapacity) {
        this(columnCount, DefaultSaveFormats.FORMAT_STANDARD, initialCapacity);
    }

    public Database(int columnCount) {
        this(columnCount, DefaultSaveFormats.FORMAT_STANDARD, 500);
    }

    public Database setCodecs(Codec... codecs) {
        assert this.codecs.length == codecs.length;
        System.arraycopy(codecs, 0, this.codecs, 0, codecs.length);
        return this;
    }

    public final int size() {
        return rows.size();
    }

    protected final void checkRowIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IllegalArgumentException(String.format("Illegal column index, not in bounds of [0,%d]: %d", columnCount, size()));
        }
    }

    public final T getRow(int index) {
        checkRowIndex(index);
        return rows.get(index);
    }

    protected final void checkColumnIndex(int index) {
        if (index < 0 || index >= columnCount) {
            throw new IllegalArgumentException(String.format("Illegal column index, not in bounds of [0,%d]: %d", columnCount, index));
        }
    }

    public final Codec getCodec(int index) {
        checkColumnIndex(index);
        return codecs[index];
    }

    protected final void checkCount(int count) {
        if (count != columnCount) {
            throw new IllegalArgumentException(String.format("Illegal number of objects in row: %d. Must be %d.", count, columnCount));
        }
    }

    public void addRow(T row) {
        checkCount(row.count());
        rows.add(row);
    }

    public void save(OutputStream out) throws IOException {
        saveFormat.write(out, this);
    }

    public void load(InputStream in) throws IOException {
        rows.clear();
        saveFormat.read(in, this);
    }

}
