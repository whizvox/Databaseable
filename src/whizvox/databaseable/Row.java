package whizvox.databaseable;

public class Row {

    private Object[] objs;

    public Row(Object... objs) {
        this.objs = objs;
    }

    public final int count() {
        return objs.length;
    }

    public Object[] getObjects() {
        return objs;
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
        final Object obj = objs[columnIndex];
        objs[columnIndex] = newObj;
        return this;
    }

}
