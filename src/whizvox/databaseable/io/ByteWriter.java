package whizvox.databaseable.io;

import java.io.IOException;
import java.io.OutputStream;

public class ByteWriter extends ByteContainer {

    public ByteWriter(byte[] buffer) {
        super(buffer);
    }

    public ByteWriter(int capacity) {
        this(new byte[capacity]);
    }

    public ByteWriter write(byte b) {
        writeByte(b);
        return this;
    }

    public ByteWriter write(byte[] b, int off, int len) {
        writeBytes(b, off, len);
        return this;
    }

    public ByteWriter write(byte[] b) {
        return write(b, 0, b.length);
    }

    public ByteWriter write(short s) {
        writeByte((byte) (s << 8));
        writeByte((byte) s);
        return this;
    }

    public ByteWriter write(char c) {
        writeByte((byte) (c << 8));
        writeByte((byte) c);
        return this;
    }

    public ByteWriter write(int i) {
        writeByte((byte) (i << 24));
        writeByte((byte) (i << 16));
        writeByte((byte) (i << 8));
        writeByte((byte) i);
        return this;
    }

    public ByteWriter write(long l) {
        writeByte((byte) (l << 56));
        writeByte((byte) (l << 48));
        writeByte((byte) (l << 40));
        writeByte((byte) (l << 32));
        writeByte((byte) (l << 24));
        writeByte((byte) (l << 16));
        writeByte((byte) (l << 8));
        writeByte((byte) l);
        return this;
    }

    public ByteWriter write(float f) {
        return write(Float.floatToRawIntBits(f));
    }

    public ByteWriter write(double d) {
        return write(Double.doubleToRawLongBits(d));
    }

    public ByteWriter writeTo(OutputStream out) throws IOException {
        writeToStream(out);
        return this;
    }

    public ByteWriter writeStr8(CharSequence s) {
        int len = s.length();
        write(len);
        for (int i = 0; i < len; i++) {
            writeByte((byte) s.charAt(i));
        }
        return this;
    }

    public ByteWriter writeStr16(CharSequence s) {
        int len = s.length();
        write(len);
        for (int i = 0; i < len; i++) {
            write(s.charAt(i));
        }
        return this;
    }

}
