package whizvox.databaseable.query;

import whizvox.databaseable.Database;
import whizvox.databaseable.Row;

import java.util.*;

public class ObjectOutput<T extends Object> implements Iterable<T> {

    private List<T> list = new ArrayList<>();
    private ObjectOutputIterator iter = new ObjectOutputIterator();
    private int limit = -1;

    public void add(T obj) {
        list.add(obj);
    }

    public void putAll(Database<? extends Row> db) {
        list.clear();
        for (int i = 0; i < db.getRowCount(); i++) {
            list.add((T) db.get(i));
        }
    }

    public int size() {
        return limit != -1 ? limit : list.size();
    }

    public void limit(int newLimit) {
        limit = newLimit;
    }

    public void cut(int point) {
        list.subList(point, list.size() - 1).clear();
    }

    public void sort(Comparator<T> comparator) {
        list.sort(comparator);
    }

    @Override
    public Iterator<T> iterator() {
        iter.index = 0;
        return iter;
    }

    private class ObjectOutputIterator implements Iterator<T> {
        int index;
        @Override
        public boolean hasNext() {
            return index < size();
        }
        @Override
        public T next() {
            return list.get(index++);
        }
    }

}
