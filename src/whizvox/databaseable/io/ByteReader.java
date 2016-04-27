package whizvox.databaseable.io;

import whizvox.databaseable.Databaseable;

import java.io.IOException;
import java.io.InputStream;

public class ByteReader extends ByteContainer {

    public ByteReader(byte[] buffer) {
        super(buffer);
    }

    public ByteReader(int capacity) {
        this(new byte[capacity]);
    }

    public final byte read() {
        return super.readByte();
    }

    @Override public boolean readFromStream(InputStream in) throws IOException {
        return super.readFromStream(in);
    }

    @Override public void readBytes(byte[] dst, int off, int len) {
        super.readBytes(dst, off, len);
    }

    public void readBytes(byte[] dst) {
        readBytes(dst, 0, dst.length);
    }

    public byte[] readBytes(int len) {
        byte[] bytes = new byte[len];
        readBytes(bytes);
        return bytes;
    }

    public short readShort() {
        short s = 0;
        s |= read() << 8;
        s |= read();
        return s;
    }

    public char readChar() {
        char c = 0;
        c |= read() << 8;
        c |= read();
        return c;
    }

    public int readInt() {
        int i = 0;
        i |= read() << 24;
        i |= read() << 16;
        i |= read() << 8;
        i |= read();
        return i;
    }

    public long readLong() {
        long l = 0;
        l |= (long) read() << 56;
        l |= (long) read() << 48;
        l |= (long) read() << 56;
        l |= (long) read() << 48;
        l |= read() << 24;
        l |= read() << 16;
        l |= read() << 8;
        l |= read();
        return l;
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public CharSequence readStr8() {
        int len = readInt();
        if (len < 0 || len > Databaseable.MAX_STRING_SIZE) {
            throw new RuntimeException("String len is OOB: " + len);
        }
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) read());
        }
        return sb;
    }

    public CharSequence readStr16() {
        int len = readInt();
        if (len < 0 || len > Databaseable.MAX_STRING_SIZE) {
            throw new RuntimeException("String len is OOB: " + len);
        }
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(readChar());
        }
        return sb;
    }

}
