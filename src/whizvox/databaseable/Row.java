package whizvox.databaseable;

public class Row {

    private Object[] objs;

    /**
     * All instances of this REQUIRE an empty constructor
     */
    public Row() {

    }

    public Row setObjects(Object... objs) {
        this.objs = objs;
        return this;
    }

    public Row allocate(int size) {
        return setObjects(new Object[size]);
    }

    public final int count() {
        return objs.length;
    }

    public void set(int columnIndex, Object obj) {
        objs[columnIndex] = obj;
    }

    public Object remove(int columnIndex) {
        final Object obj = objs[columnIndex];
        objs[columnIndex] = null;
        return obj;
    }

    public Object replace(int columnIndex, Object newObj) {
        Object oldObj = remove(columnIndex);
        set(columnIndex, newObj);
        return oldObj;
    }

    public Object getObject(int index) {
        return objs[index];
    }

}
