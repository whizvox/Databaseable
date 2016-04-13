package whizvox.databaseable;

import whizvox.databaseable.codec.DbCodec;
import whizvox.databaseable.io.FileIOGenerator;
import whizvox.databaseable.io.IOGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database<T extends Row> {

    private List<T> rows = new ArrayList<>();
    private int keyColumn = 0, version = 0;
    private String[] names;
    private DbCodec[] codecs;
    private IOGenerator ioGenerator = null;
    private SaveFormat saveFormat;
    private final Class<T> rowClass;
    private Date lastSaved = null;

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

    public Database<T> setVersion(int version) {
        this.version = version;
        return this;
    }

    public Database<T> setKeyColumn(int index) {
        checkColumnIndex(index);
        this.keyColumn = index;
        return this;
    }

    public Database<T> setColumnNames(String... names) {
        assert columnCount == names.length;
        this.names = new String[columnCount];
        System.arraycopy(names, 0, this.names, 0, columnCount);
        return this;
    }

    public Database<T> setCodecs(DbCodec... codecs) {
        assert columnCount == codecs.length;
        this.codecs = new DbCodec[columnCount];
        System.arraycopy(codecs, 0, this.codecs, 0, columnCount);
        return this;
    }

    public Database<T> setIOGenerator(IOGenerator ioGenerator) {
        this.ioGenerator = ioGenerator;
        return this;
    }

    public Database<T> setFile(File file) {
        return setIOGenerator(new FileIOGenerator(file));
    }

    public Database<T> setSaveFormat(SaveFormat saveFormat) {
        this.saveFormat = saveFormat;
        return this;
    }

    public Database<T> setSaveFormatBufferSize(int bufferSize) {
        return setSaveFormat(new SaveFormat(bufferSize));
    }

    public Database<T> setDefaultSaveFormat() {
        return setSaveFormatBufferSize(1024);
    }

    public final int getColumnCount() {
        return columnCount;
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

    public void save(OutputStream out) throws IOException {
        saveFormat.save(this, out);
    }

    public void save(File file) throws IOException {
        save(new FileOutputStream(file));
    }

    public void save() throws IOException {
        save(ioGenerator.generateOutputStream());
    }

    public void load(InputStream in) throws IOException {
        saveFormat.load(this, in);
    }

    public void load(File file) throws IOException {
        load(new FileInputStream(file));
    }

    public void load() throws IOException {
        load(ioGenerator.generateInputStream());
    }

    protected void setLastSaved(Date lastSaved) {
        this.lastSaved = lastSaved;
    }

    protected String[] getNames() {
        return names;
    }

    protected DbCodec[] getCodecs() {
        return codecs;
    }

    protected void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

}
