package whizvox.databaseable;

import whizvox.databaseable.codec.DataCodec;
import whizvox.databaseable.io.FileIOGenerator;
import whizvox.databaseable.io.IOGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Database<T extends Row> {

    private List<T> rows = new ArrayList<>();
    private int keyColumn = 0, version = 0;
    private String[] names;
    private DataCodec[] codecs;
    private IOGenerator ioGenerator = null;
    private SaveFormat saveFormat = null;
    private final Class<T> rowClass;
    private Date lastSaved = null;
    private boolean busy = false;

    private int columnCount;

    public Database(int columnCount, Class<T> rowClass) {
        this.columnCount = columnCount;
        this.rowClass = rowClass;
    }

    public final Class<T> getRowClass() {
        return rowClass;
    }

    protected final void checkColumnIndex(int index) {
        if (index < 0 || index >= columnCount) {
            throw new IllegalArgumentException(String.format("Column index does not fall within bounds of [0,%d]: %d", columnCount - 1, index));
        }
    }

    protected final void checkRowIndex(int index) {
        if (index < 0 || index >= rows.size()) {
            throw new IllegalArgumentException(String.format("Row index does not fall within bounds of [0,%d]: %d", rows.size() - 1, index));
        }
    }

    public int getVersion() {
        return version;
    }

    public void checkVersion(int detectedVersion) {
        if (detectedVersion != version) {
            throw new InvalidDataException("Required version: " + version + ", Detected version: " + detectedVersion);
        }
    }

    public Database<T> version(int version) {
        this.version = version;
        return this;
    }

    public Database<T> keyColumn(int index) {
        checkColumnIndex(index);
        this.keyColumn = index;
        return this;
    }

    public Database<T> names(String... names) {
        assert columnCount == names.length;
        this.names = new String[columnCount];
        System.arraycopy(names, 0, this.names, 0, columnCount);
        return this;
    }

    public Database<T> codecs(DataCodec... codecs) {
        assert columnCount == codecs.length;
        this.codecs = new DataCodec[columnCount];
        System.arraycopy(codecs, 0, this.codecs, 0, columnCount);
        return this;
    }

    public Database<T> ioGenerator(IOGenerator ioGenerator) {
        this.ioGenerator = ioGenerator;
        return this;
    }

    public Database<T> file(File file) {
        return ioGenerator(new FileIOGenerator(file));
    }

    public Database<T> saveFormat(SaveFormat saveFormat) {
        this.saveFormat = saveFormat;
        return this;
    }

    public Database<T> bufferSize(int bufferSize) {
        return saveFormat(new SaveFormat(bufferSize));
    }

    public Database<T> defaultSaveFormat() {
        return bufferSize(1024);
    }

    public final int getColumnCount() {
        return columnCount;
    }

    public final int getColumnIndex(String name) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public final boolean hasColumn(String name) {
        return getColumnIndex(name) != -1;
    }

    public Date getLastSaved() {
        return lastSaved;
    }

    public int getRowCount() {
        return rows.size();
    }

    public T get(int index) {
        checkRowIndex(index);
        return rows.get(index);
    }

    public void add(T row) {
        rows.add(row);
    }

    public void clear() {
        rows.clear();
    }

    public void save(OutputStream out) throws IOException {
        if (saveFormat == null) {
            saveFormat = new SaveFormat();
        }
        if (busy) {
            throw new IllegalStateException("Database is busy either reading or writing");
        }
        busy = true;
        saveFormat.save(this, out);
        busy = false;
    }

    public void save(File file) throws IOException {
        save(new FileOutputStream(file));
    }

    public void save() throws IOException {
        save(ioGenerator.generateOutputStream());
    }

    public void load(InputStream in) throws IOException {
        if (saveFormat == null) {
            saveFormat = new SaveFormat();
        }
        if (busy) {
            throw new IllegalStateException("Database is busy either reading or writing");
        }
        busy = true;
        saveFormat.load(this, getRowClass(), in);
        busy = false;
    }

    public void load(File file) throws IOException {
        load(new FileInputStream(file));
    }

    public void load() throws IOException {
        load(ioGenerator.generateInputStream());
    }

    public DataCodec getCodec(int columnIndex) {
        checkColumnIndex(columnIndex);
        return codecs[columnIndex];
    }

    public DataCodec getCodec(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        checkColumnIndex(columnIndex);
        return codecs[columnIndex];
    }

    public String getName(int columnIndex) {
        checkColumnIndex(columnIndex);
        return names[columnIndex];
    }

    protected void setLastSaved(Date lastSaved) {
        this.lastSaved = lastSaved;
    }

    protected String[] getNames() {
        return names;
    }

    protected DataCodec[] getCodecs() {
        return codecs;
    }

    protected void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    @Override
    public String toString() {
        return "Database(names=" + Arrays.toString(names) + ",codecs=" + Arrays.toString(codecs) + ",rows=" + getRowCount() + ")";
    }

}
