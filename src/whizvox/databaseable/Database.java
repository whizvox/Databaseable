package whizvox.databaseable;

import whizvox.databaseable.standards.BufferedSaveFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database<T extends Row> {

    public final int columnCount;
    private final Codec[] codecs;
    private final String[] names;
    private final SaveFormat saveFormat;
    private IOGenerator source = null;
    private int keyColumnIndex = 0;
    private final List<T> rows;
    public final Class<T> rowClass;

    public Database(int columnCount, SaveFormat saveFormat, int initialCapacity, Class<T> rowClass) {
        this.columnCount = columnCount;
        codecs = new Codec[columnCount];
        names = new String[columnCount];
        this.saveFormat = saveFormat;
        rows = new ArrayList<>(initialCapacity);
        this.rowClass = rowClass;
    }

    public Database(int columnCount, int initialCapacity, Class<T> rowClass) {
        this(columnCount, new BufferedSaveFormat(), initialCapacity, rowClass);
    }

    public Database(int columnCount, Class<T> rowClass) {
        this(columnCount, 500, rowClass);
    }

    public Database<T> setCodecs(Codec... codecs) {
        assert this.codecs.length == codecs.length;
        System.arraycopy(codecs, 0, this.codecs, 0, codecs.length);
        return this;
    }

    public Database<T> setNames(String... names) {
        assert this.names.length == names.length;
        System.arraycopy(names, 0, this.names, 0, names.length);
        return this;
    }

    public Database<T> setSource(IOGenerator ioGenerator) {
        source = ioGenerator;
        return this;
    }

    public Database<T> setKeyColumn(int columnIndex) {
        checkColumnIndex(columnIndex);
        keyColumnIndex = columnIndex;
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

    public T getFromIndex(int index) {
        checkRowIndex(index);
        return rows.get(index);
    }

    public T get(int columnIndex, Object key) {
        for (T row : rows) {
            if (row.getObject(columnIndex).equals(key)) {
                return row;
            }
        }
        return null;
    }

    public T get(Object key) {
        return get(keyColumnIndex, key);
    }

    public void clear() {
        rows.clear();
    }

    public void save(OutputStream out) throws IOException {
        saveFormat.write(out, this);
    }

    public void save() {
        try (OutputStream out = source.genOutputStream()) {
            save(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(InputStream in) throws IOException {
        rows.clear();
        saveFormat.read(in, this);
    }

    public void load(File file) {
        try (InputStream in = new FileInputStream(file)) {
            load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        try (InputStream in = source.genInputStream()) {
            load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
