package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StandardCodecs {

    private StandardCodecs() {}

    public static final DbCodec<Boolean> CODEC_BOOL = new DbCodec<Boolean>() {
        @Override public int length() {
            return 1;
        }
        @Override public void write(ByteBuffer buffer, Boolean obj) {
            buffer.put((byte) (obj ? 1 : 0));
        }
        @Override public Boolean read(ByteBuffer buffer) throws InvalidDataException {
            byte b = buffer.get();
            if (b == 0) {
                return false;
            } else if (b == 1) {
                return true;
            }
            throw new InvalidDataException("Invalid boolean: " + b);
        }
    };

    public static final DbCodec<Byte> CODEC_BYTE = new DbCodec<Byte>() {
        @Override public int length() {
            return Byte.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Byte obj) {
            buffer.put(obj);
        }
        @Override public Byte read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.get();
        }
    };

    public static final DbCodec<Short> CODEC_SHORT = new DbCodec<Short>() {
        @Override public int length() {
            return Short.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Short obj) {
            buffer.putShort(obj);
        }
        @Override public Short read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.getShort();
        }
    };

    public static final DbCodec<Integer> CODEC_INT = new DbCodec<Integer>() {
        @Override public int length() {
            return Integer.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Integer obj) {
            buffer.putInt(obj);
        }
        @Override public Integer read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.getInt();
        }
    };

    public static final DbCodec<Long> CODEC_LONG = new DbCodec<Long>() {
        @Override public int length() {
            return Long.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Long obj) {
            buffer.putLong(obj);
        }
        @Override public Long read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.getLong();
        }
    };

    public static final DbCodec<Float> CODEC_FLOAT = new DbCodec<Float>() {
        @Override public int length() {
            return Float.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Float obj) {
            buffer.putFloat(obj);
        }
        @Override public Float read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.getFloat();
        }
    };

    public static final DbCodec<Double> CODEC_DOUBLE = new DbCodec<Double>() {
        @Override public int length() {
            return Double.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Double obj) {
            buffer.putDouble(obj);
        }
        @Override public Double read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.getDouble();
        }
    };

    public static final DbCodec<Character> CODEC_CHAR = new DbCodec<Character>() {
        @Override public int length() {
            return Character.BYTES;
        }
        @Override public void write(ByteBuffer buffer, Character obj) {
            buffer.putChar(obj);
        }
        @Override public Character read(ByteBuffer buffer) throws InvalidDataException {
            return buffer.getChar();
        }
    };

    public static final DbCodec<Date> CODEC_DATE = new DbCodec<Date>() {
        @Override public int length() {
            return CODEC_LONG.length();
        }
        @Override public void write(ByteBuffer buffer, Date obj) {
            CODEC_LONG.write(buffer, obj.getTime());
        }
        @Override public Date read(ByteBuffer buffer) throws InvalidDataException {
            return new Date(CODEC_LONG.read(buffer));
        }
    };

    public static final DbCodec<UUID> CODEC_UUID = new DbCodec<UUID>() {
        @Override public int length() {
            return CODEC_LONG.length() * 2;
        }
        @Override public void write(ByteBuffer buffer, UUID obj) {
            buffer.putLong(obj.getMostSignificantBits());
            buffer.putLong(obj.getLeastSignificantBits());
        }
        @Override public UUID read(ByteBuffer buffer) throws InvalidDataException {
            return new UUID(buffer.getLong(), buffer.getLong());
        }
    };

    public static final DbCodec<InetAddress> CODEC_INET_ADDRESS = new DbCodec<InetAddress>() {
        @Override public int length() {
            return 4;
        }
        @Override public void write(ByteBuffer buffer, InetAddress obj) {
            buffer.put(obj.getAddress());
        }
        @Override public InetAddress read(ByteBuffer buffer) throws InvalidDataException {
            byte[] addressBuf = new byte[length()];
            buffer.get(addressBuf);
            try {
                return InetAddress.getByAddress(addressBuf);
            } catch (UnknownHostException e) {
                throw new InvalidDataException(e);
            }
        }
    };

    public static final DbCodec<String> CODEC_STRING_8BIT = new VaryingLengthCodec<String>() {
        @Override
        public int length(String obj) {
            return obj.length();
        }
        @Override
        public int size(String obj) {
            return obj.length();
        }
        @Override
        protected void write_do(ByteBuffer buffer, String obj) {
            for (int i = 0; i < obj.length(); i++) {
                buffer.put((byte) obj.charAt(i));
            }
        }
        @Override
        protected String read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            char[] chars = new char[size];
            for (int i = 0; i < size; i++) {
                chars[i] = (char) buffer.get();
            }
            return new String(chars);
        }
    };

    public static final DbCodec<String> CODEC_STRING_16BIT = new VaryingLengthCodec<String>() {
        @Override
        public int length(String obj) {
            return obj.length() * 2;
        }
        @Override
        public int size(String obj) {
            return obj.length();
        }
        @Override
        protected void write_do(ByteBuffer buffer, String obj) {
            for (int i = 0; i < obj.length(); i++) {
                char c = obj.charAt(i);
                buffer.put((byte) (c >>> 8));
                buffer.put((byte) c);
            }
        }
        @Override
        protected String read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            char[] chars = new char[size];
            for (int i = 0; i < size; i++) {
                chars[i] = (char) (buffer.get() << 8 | buffer.get() & 0xff);
            }
            return new String(chars);
        }
    };

    public static final DbCodec<boolean[]> CODEC_ARRAY_BOOL = new VaryingLengthCodec<boolean[]>() {
        @Override public int length(boolean[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteBuffer buffer, boolean[] obj) {
            for (boolean b : obj) {
                CODEC_BOOL.write(buffer, b);
            }
        }
        @Override protected boolean[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            boolean[] bools = new boolean[size];
            for (int i = 0; i < size; i++) {
                bools[i] = CODEC_BOOL.read(buffer);
            }
            return bools;
        }
    };

    public static final DbCodec<byte[]> CODEC_ARRAY_BYTE = new VaryingLengthCodec<byte[]>() {
        @Override public int length(byte[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteBuffer buffer, byte[] obj) {
            for (byte b : obj) {
                CODEC_BYTE.write(buffer, b);
            }
        }
        @Override protected byte[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            byte[] bytes = new byte[size];
            for (int i = 0; i < size; i++) {
                bytes[i] = CODEC_BYTE.read(buffer);
            }
            return bytes;
        }
    };

    public static final DbCodec<short[]> CODEC_ARRAY_SHORT = new VaryingLengthCodec<short[]>() {
        @Override public int length(short[] obj) {
            return obj.length * CODEC_SHORT.length();
        }
        @Override protected void write_do(ByteBuffer buffer, short[] obj) {
            for (short s : obj) {
                CODEC_SHORT.write(buffer, s);
            }
        }
        @Override protected short[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            short[] shorts = new short[size];
            for (int i = 0; i < size; i++) {
                shorts[i] = CODEC_SHORT.read(buffer);
            }
            return shorts;
        }
    };

    public static final DbCodec<int[]> CODEC_ARRAY_INT = new VaryingLengthCodec<int[]>() {
        @Override public int length(int[] obj) {
            return obj.length * CODEC_INT.length();
        }
        @Override protected void write_do(ByteBuffer buffer, int[] obj) {
            for (int i : obj) {
                CODEC_INT.write(buffer, i);
            }
        }
        @Override protected int[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            int[] ints = new int[size];
            for (int i = 0; i < size; i++) {
                ints[i] = CODEC_INT.read(buffer);
            }
            return ints;
        }
    };

    public static final DbCodec<long[]> CODEC_ARRAY_LONG = new VaryingLengthCodec<long[]>() {
        @Override public int length(long[] obj) {
            return obj.length * CODEC_LONG.length();
        }
        @Override protected void write_do(ByteBuffer buffer, long[] obj) {
            for (long l : obj) {
                CODEC_LONG.write(buffer, l);
            }
        }
        @Override protected long[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            long[] longs = new long[size];
            for (int i = 0; i < size; i++) {
                longs[i] = CODEC_LONG.read(buffer);
            }
            return longs;
        }
    };

    public static final DbCodec<float[]> CODEC_ARRAY_FLOAT = new VaryingLengthCodec<float[]>() {
        @Override public int length(float[] obj) {
            return obj.length * CODEC_FLOAT.length();
        }
        @Override protected void write_do(ByteBuffer buffer, float[] obj) {
            for (float f : obj) {
                CODEC_FLOAT.write(buffer, f);
            }
        }
        @Override protected float[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            float[] floats = new float[size];
            for (int i = 0; i < size; i++) {
                floats[i] = CODEC_FLOAT.read(buffer);
            }
            return floats;
        }
    };

    public static final DbCodec<double[]> CODEC_ARRAY_DOUBLE = new VaryingLengthCodec<double[]>() {
        @Override public int length(double[] obj) {
            return obj.length * CODEC_DOUBLE.length();
        }
        @Override protected void write_do(ByteBuffer buffer, double[] obj) {
            for (double d : obj) {
                CODEC_DOUBLE.write(buffer, d);
            }
        }
        @Override protected double[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            double[] doubles = new double[size];
            for (int i = 0; i < size; i++) {
                doubles[i] = CODEC_DOUBLE.read(buffer);
            }
            return doubles;
        }
    };

    public static final DbCodec<char[]> CODEC_ARRAY_CHAR = new VaryingLengthCodec<char[]>() {
        @Override public int length(char[] obj) {
            return obj.length * CODEC_CHAR.length();
        }
        @Override protected void write_do(ByteBuffer buffer, char[] obj) {
            for (char c : obj) {
                CODEC_CHAR.write(buffer, c);
            }
        }
        @Override protected char[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            char[] chars = new char[size];
            for (int i = 0; i < size; i++) {
                chars[i] = CODEC_CHAR.read(buffer);
            }
            return chars;
        }
    };

    public static final DbCodec<Date[]> CODEC_ARRAY_DATE = new VaryingLengthCodec<Date[]>() {
        @Override public int length(Date[] obj) {
            return obj.length * CODEC_DATE.length();
        }
        @Override protected void write_do(ByteBuffer buffer, Date[] obj) {
            for (Date date : obj) {
                CODEC_DATE.write(buffer, date);
            }
        }
        @Override protected Date[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            Date[] dates = new Date[size];
            for (int i = 0; i < size; i++) {
                dates[i] = CODEC_DATE.read(buffer);
            }
            return dates;
        }
    };

    public static final DbCodec<UUID[]> CODEC_ARRAY_UUID = new VaryingLengthCodec<UUID[]>() {
        @Override public int length(UUID[] obj) {
            return obj.length * CODEC_UUID.length();
        }
        @Override protected void write_do(ByteBuffer buffer, UUID[] obj) {
            for (UUID uuid : obj) {
                CODEC_UUID.write(buffer, uuid);
            }
        }
        @Override protected UUID[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            UUID[] uuids = new UUID[size];
            for (int i = 0; i < size; i++) {
                uuids[i] = CODEC_UUID.read(buffer);
            }
            return uuids;
        }
    };

    public static final DbCodec<InetAddress[]> CODEC_ARRAY_INET_ADDRESS = new VaryingLengthCodec<InetAddress[]>() {
        @Override public int length(InetAddress[] obj) {
            return obj.length * CODEC_INET_ADDRESS.length();
        }
        @Override protected void write_do(ByteBuffer buffer, InetAddress[] obj) {
            for (InetAddress inetAddress : obj) {
                CODEC_INET_ADDRESS.write(buffer, inetAddress);
            }
        }
        @Override protected InetAddress[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            InetAddress[] addresses = new InetAddress[size];
            for (int i = 0; i < size; i++) {
                addresses[i] = CODEC_INET_ADDRESS.read(buffer);
            }
            return addresses;
        }
    };

    public static final DbCodec<String[]> CODEC_ARRAY_STRING_8BIT = new VaryingLengthCodec<String[]>() {
        @Override public int length(String[] obj) {
            int resultingLength = 0;
            for (String s : obj) {
                resultingLength += Integer.BYTES + CODEC_STRING_8BIT.length(s);
            }
            return resultingLength;
        }
        @Override public int size(String[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteBuffer buffer, String[] obj) {
            for (String s : obj) {
                CODEC_STRING_8BIT.write(buffer, s);
            }
        }
        @Override protected String[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            String[] strings = new String[size];
            for (int i = 0; i < size; i++) {
                strings[i] = CODEC_STRING_8BIT.read(buffer);
            }
            return strings;
        }
    };

    public static final DbCodec<String[]> CODEC_ARRAY_STRING_16BIT = new VaryingLengthCodec<String[]>() {
        @Override public int length(String[] obj) {
            int resultingLength = 0;
            for (String s : obj) {
                resultingLength += Integer.BYTES + CODEC_STRING_16BIT.length(s);
            }
            return resultingLength;
        }
        @Override public int size(String[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteBuffer buffer, String[] obj) {
            for (String s : obj) {
                CODEC_STRING_16BIT.write(buffer, s);
            }
        }
        @Override protected String[] read_do(int size, ByteBuffer buffer) throws InvalidDataException {
            String[] strings = new String[size];
            for (int i = 0; i < size; i++) {
                strings[i] = CODEC_STRING_16BIT.read(buffer);
            }
            return strings;
        }
    };

    public static final DbCodec<List<Boolean>> CODEC_LIST_BOOL = new CodecList<>(CODEC_BOOL);

    public static final DbCodec<List<Byte>> CODEC_LIST_BYTE = new CodecList<>(CODEC_BYTE);

    public static final DbCodec<List<Short>> CODEC_LIST_SHORT = new CodecList<>(CODEC_SHORT);

    public static final DbCodec<List<Integer>> CODEC_LIST_INT = new CodecList<>(CODEC_INT);

    public static final DbCodec<List<Long>> CODEC_LIST_LONG = new CodecList<>(CODEC_LONG);

    public static final DbCodec<List<Float>> CODEC_LIST_FLOAT = new CodecList<>(CODEC_FLOAT);

    public static final DbCodec<List<Double>> CODEC_LIST_DOUBLE = new CodecList<>(CODEC_DOUBLE);

    public static final DbCodec<List<Character>> CODEC_LIST_CHAR = new CodecList<>(CODEC_CHAR);

    public static final DbCodec<List<Date>> CODEC_LIST_DATE = new CodecList<>(CODEC_DATE);

    public static final DbCodec<List<UUID>> CODEC_LIST_UUID = new CodecList<>(CODEC_UUID);

    public static final DbCodec<List<InetAddress>> CODEC_LIST_INET_ADDRESS = new CodecList<>(CODEC_INET_ADDRESS);

    public static final DbCodec<List<String>> CODEC_LIST_STRING_UTF8 = new CodecList<>(CODEC_STRING_8BIT);

    public static final DbCodec<List<String>> CODEC_LIST_STRING_UTF16 = new CodecList<>(CODEC_STRING_16BIT);

}
