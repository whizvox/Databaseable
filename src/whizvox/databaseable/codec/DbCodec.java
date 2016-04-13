package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;

import java.nio.ByteBuffer;

public interface DbCodec<T> {

    int length();

    default int length(T obj) {
        return length();
    }

    default boolean lengthCanVary() {
        return false;
    }

    void write(ByteBuffer buffer, T obj);

    T read(ByteBuffer buffer) throws InvalidDataException;

}
