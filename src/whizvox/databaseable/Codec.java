package whizvox.databaseable;

import java.nio.CharBuffer;

public interface Codec<T> {

    Class<T> getObjectClass();

    void write(CharBuffer out, T obj);

    T read(CharBuffer in);

}
