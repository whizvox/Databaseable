package whizvox.databaseable;

public interface Codec<T> {

    Class<T> getObjectClass();

    CharSequence write(T obj);

    T read(CharSequence s);

    boolean requiresSanitation();

}
