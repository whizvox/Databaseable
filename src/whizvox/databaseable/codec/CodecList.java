package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class CodecList<T> extends VaryingLengthCodec<List<T>> {

    public final DbCodec<T> origCodec;

    public CodecList(DbCodec<T> origCodec) {
        this.origCodec = origCodec;
    }

    @Override public int length(List<T> obj) {
        int resultingLength = Integer.BYTES;
        if (origCodec.lengthCanVary()) {
            for (T t : obj) {
                resultingLength += origCodec.length(t);
            }
        } else {
            resultingLength += obj.size() * origCodec.length();
        }
        return resultingLength;
    }

    @Override
    public int size(List<T> obj) {
        return obj.size();
    }

    @Override protected void write_do(ByteBuffer buffer, List<T> obj) {
        for (T t : obj) {
            origCodec.write(buffer, t);
        }
    }

    @Override protected List<T> read_do(int size, ByteBuffer buffer) throws InvalidDataException {
        List<T> list = new ArrayList<>(size);
        if (size < 0) {
            throw new InvalidDataException();
        }
        for (int i = 0; i < size; i++) {
            list.add(origCodec.read(buffer));
        }
        return list;
    }

}
