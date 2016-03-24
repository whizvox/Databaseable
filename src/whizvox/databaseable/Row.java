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

    public Object getObject(int index) {
        return objs[index];
    }

}
