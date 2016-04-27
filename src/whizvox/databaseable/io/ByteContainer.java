package whizvox.databaseable.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteContainer implements Cloneable {

    private byte[] buffer;

    private int pos, bitPos;

    public ByteContainer(byte[] buffer) {
        this.buffer = buffer;
        reset();
    }

    public final ByteContainer reset() {
        pos = 0;
        bitPos = 0;
        return this;
    }

    public final int capacity() {
        return buffer.length;
    }

    public final ByteContainer skip(int amount) {
        checkBufferOverflow(amount);
        pos += amount;
        return this;
    }

    protected void writeByte(int index, byte b) {
        buffer[index] = b;
    }

    protected void writeByte(byte b) {
        buffer[pos++] = b;
    }

    protected void writeBytes(byte[] b, int off, int len) {
        System.arraycopy(b, off, buffer, pos, len);
    }

    protected void writeToStream(OutputStream out) throws IOException {
        out.write(buffer, 0, pos);
        reset();
    }

    protected byte readByte() {
        return buffer[pos++];
    }

    protected void readBytes(byte[] dst, int off, int len) {
        System.arraycopy(buffer, pos, dst, off, len);
    }

    protected boolean readFromStream(InputStream in) throws IOException {
        int read;
        if (pos != 0) {
            shiftBuffer();
            read = in.read(buffer, pos, capacity() - pos);
        } else {
            read = in.read(buffer, 0, capacity());
        }
        reset();
        return read != -1;
    }

    private void checkBufferOverflow(int amount) {
        if (amount + pos > capacity()) {
            throw new RuntimeException("Buffer overflow");
        }
    }

    public final ByteContainer shiftBuffer() {
        int newPos = capacity() - pos;
        System.arraycopy(buffer, pos, buffer, 0, newPos);
        pos = newPos;
        return this;
    }

}
