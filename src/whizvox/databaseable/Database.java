package whizvox.databaseable;

import whizvox.databaseable.standards.BufferedSaveFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Database<T extends Row> {

    public final int columnCount;
    private final Codec[] codecs;
    private final String[] names;
    private final SaveFormat saveFormat;
    private IOGenerator source = null;
    private Comparator<T> sorter = null;
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

    public Database<T> setSorter(Comparator<T> sorter) {
        this.sorter = sorter;
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

    protected final void checkColumnIndex(int index) {
        if (index < 0 || index >= columnCount) {
            throw new IllegalArgumentException(String.format("Illegal column index, not in bounds of [0,%d]: %d", columnCount, index));
        }
    }

    protected final void checkCount(int count) {
        if (count != columnCount) {
            throw new IllegalArgumentException(String.format("Illegal number of objects in row: %d. Must be %d.", count, columnCount));
        }
    }

    public int getColumnIndex(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public int getRowIndex(int columnIndex, Object key) {
        checkColumnIndex(columnIndex);
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i) == null) continue;
            if (rows.get(i).getObject(columnIndex).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public int getRowIndex(String columnName, Object key) {
        return getRowIndex(getColumnIndex(columnName), key);
    }

    public int getRowIndex(Object key) {
        return getRowIndex(keyColumnIndex, key);
    }

    public Codec getCodec(int index) {
        checkColumnIndex(index);
        return codecs[index];
    }

    public Codec getCodec(String name) {
        return getCodec(getColumnIndex(name));
    }

    public void add(T row) {
        checkCount(row.count());
        rows.add(row);
    }

    public void addAll(Iterable<T> rows) {
        for (T row : rows) {
            add(row);
        }
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

    public T get(String columnName, Object key) {
        return get(getColumnIndex(columnName), key);
    }

    public T get(Object key) {
        return get(keyColumnIndex, key);
    }

    public T removeFromIndex(int index) {
        checkRowIndex(index);
        return rows.remove(index);
    }

    public T remove(int columnIndex, Object key) {
        int index;
        checkRowIndex(index = getRowIndex(columnIndex, key));
        return rows.remove(index);
    }

    public T remove(String columnName, Object key) {
        return remove(getColumnIndex(columnName), key);
    }

    public T remove(Object key) {
        return remove(keyColumnIndex, key);
    }

    public T replace(int index, T newRow) {
        checkRowIndex(index);
        T old = rows.remove(index);
        rows.set(index, newRow);
        return old;
    }

    public T replace(int columnIndex, Object key, T newRow) {
        return replace(getRowIndex(columnIndex, key), newRow);
    }

    public T replace(String columnName, Object key, T newRow) {
        return replace(getColumnIndex(columnName), key, newRow);
    }

    public T replace(Object key, T newRow) {
        return replace(getRowIndex(key), newRow);
    }

    public void clear() {
        rows.clear();
    }

    public void sort(Comparator<T> comparator) {
        rows.sort(comparator);
    }

    public void sort() {
        sort(sorter);
    }

    public void save(OutputStream out) throws IOException {
        saveFormat.write(out, this);
    }

    public void save(File file) {
        try (OutputStream out = new FileOutputStream(file)) {
            save(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
