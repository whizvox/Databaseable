package whizvox.databaseable_old;

public interface Codec<T> {

    Class<T> getObjectClass();

    String write(T obj);

    T read(String s);

    default boolean requiresSanitation() {
        return false;
    }

}
