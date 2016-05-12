package whizvox.databaseable;

import java.util.Arrays;

public class Row {

    private Object[] objects;

    public Row(Object... objects) {
        assert objects.length > 0;
        this.objects = objects;
    }

    public Row(int size) {
        this(new Object[size]);
    }

    public final int size() {
        return objects.length;
    }

    protected final void checkIndex(int index) {
        if (index < 0 || index >= size()) {
            throw new IllegalArgumentException(String.format("Index is not within bounds of [0,%d]: %d", size() - 1, index));
        }
    }

    public Object get(int index) {
        checkIndex(index);
        return objects[index];
    }

    public Object setObject(int index, Object newObject) {
        Object old = get(index);
        objects[index] = newObject;
        return old;
    }

    @Override
    public String toString() {
        return "Row(" + Arrays.toString(objects) + ')';
    }

}
