package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;
import whizvox.databaseable.io.ByteReader;
import whizvox.databaseable.io.ByteWriter;

import java.util.ArrayList;
import java.util.List;

public final class CodecList<T> extends VaryingLengthCodec<List<T>> {

    public final DataCodec<T> origCodec;

    public CodecList(DataCodec<T> origCodec) {
        this.origCodec = origCodec;
    }

    @Override public int size(List<T> obj) {
        return obj.size();
    }

    @Override protected void write_do(ByteWriter writer, List<T> obj) {
        for (T t : obj) {
            origCodec.write(writer, t);
        }
    }

    @Override protected List<T> read_do(int size, ByteReader reader) throws InvalidDataException {
        List<T> list = new ArrayList<>(size);
        if (size < 0) {
            throw new InvalidDataException();
        }
        for (int i = 0; i < size; i++) {
            list.add(origCodec.read(reader));
        }
        return list;
    }

}
