package whizvox.databaseable.codec;

import whizvox.databaseable.Databaseable;
import whizvox.databaseable.InvalidDataException;
import whizvox.databaseable.io.ByteReader;
import whizvox.databaseable.io.ByteWriter;

public abstract class VaryingLengthCodec<T> implements DataCodec<T> {

    public abstract int size(T obj);

    @Override public final void write(ByteWriter writer, T obj) {
        int size = size(obj);
        if (size < 0 || size > Databaseable.MAX_DATA_SIZE) {
            throw new InvalidDataException(String.format("Varying data size not within bounds of [0,%d]: %d", Databaseable.MAX_DATA_SIZE, size));
        }
        writer.write(size);
        write_do(writer, obj);
    }

    protected abstract void write_do(ByteWriter writer, T obj);

    @Override public final T read(ByteReader reader) throws InvalidDataException {
        int size = reader.readInt();
        if (size < 0 || size > Databaseable.MAX_DATA_SIZE) {
            throw new InvalidDataException(String.format("Varying data size not within bounds of [0,%d]: %d", Databaseable.MAX_DATA_SIZE, size));
        }
        return read_do(size, reader);
    }

    protected abstract T read_do(int size, ByteReader reader) throws InvalidDataException;

}
