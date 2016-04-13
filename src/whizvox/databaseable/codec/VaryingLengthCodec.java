package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;

import java.nio.ByteBuffer;

public abstract class VaryingLengthCodec<T> implements DbCodec<T> {

    @Override
    public final boolean lengthCanVary() {
        return true;
    }

    @Override
    public final int length() {
        throw new UnsupportedOperationException();
    }

    public abstract int length(T obj);

    /* Number of elements in the varying object. Used for lists and arrays. */
    public int size(T obj) {
        return length(obj);
    }

    @Override
    public final void write(ByteBuffer buffer, T obj) {
        buffer.putInt(size(obj));
        write_do(buffer, obj);
    }

    protected abstract void write_do(ByteBuffer buffer, T obj);

    @Override
    public final T read(ByteBuffer buffer) throws InvalidDataException {
        int size = buffer.getInt();
        return read_do(size, buffer);
    }

    protected abstract T read_do(int size, ByteBuffer buffer) throws InvalidDataException;

}
