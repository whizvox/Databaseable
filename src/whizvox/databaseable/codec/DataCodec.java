package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;
import whizvox.databaseable.io.ByteReader;
import whizvox.databaseable.io.ByteWriter;

import java.nio.ByteBuffer;

public interface DataCodec<T> {

    void write(ByteWriter writer, T obj);

    T read(ByteReader reader) throws InvalidDataException;

}
