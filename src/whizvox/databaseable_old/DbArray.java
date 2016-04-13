package whizvox.databaseable_old;

public class DbArray<T> {

    private Object[] array;
    private Codec<T> codec;

    public DbArray(T[] array, Codec<T> codec) {
        this.array = array;
        this.codec = codec;
    }

    public DbArray(int size, Codec<T> codec) {
        this.array = new Object[size];
        this.codec = codec;
    }

    /**
     * Will throw an exception unless the {@link #DbArray(Object[], Codec)} constructor was used
     */
    @SuppressWarnings("unchecked")
    public T[] getArray() {
        return (T[]) array;
    }

    public int length() {
        return array.length;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) array[index];
    }

    public T set(int index, T newObj) {
        T obj = get(index);
        array[index] = newObj;
        return obj;
    }

    public Codec<T> getCodec() {
        return codec;
    }

}
