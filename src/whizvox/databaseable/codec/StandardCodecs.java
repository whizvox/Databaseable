package whizvox.databaseable.codec;

import whizvox.databaseable.InvalidDataException;
import whizvox.databaseable.io.ByteReader;
import whizvox.databaseable.io.ByteWriter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StandardCodecs {

    private StandardCodecs() {}

    public static final DataCodec<Boolean> CODEC_BOOL = new DataCodec<Boolean>() {
        @Override public void write(ByteWriter writer, Boolean obj) {
            writer.write((byte) (obj ? 1 : 0));
        }
        @Override public Boolean read(ByteReader reader) throws InvalidDataException {
            byte b = reader.read();
            if (b == 0) {
                return false;
            } else if (b == 1) {
                return true;
            }
            throw new InvalidDataException("Invalid boolean: " + b);
        }
    };

    public static final DataCodec<Byte> CODEC_BYTE = new DataCodec<Byte>() {
        @Override public void write(ByteWriter writer, Byte obj) {
            writer.write(obj);
        }
        @Override public Byte read(ByteReader reader) throws InvalidDataException {
            return reader.read();
        }
    };

    public static final DataCodec<Short> CODEC_SHORT = new DataCodec<Short>() {
        @Override public void write(ByteWriter writer, Short obj) {
            writer.write(obj);
        }
        @Override public Short read(ByteReader reader) throws InvalidDataException {
            return reader.readShort();
        }
    };

    public static final DataCodec<Integer> CODEC_INT = new DataCodec<Integer>() {
        @Override public void write(ByteWriter writer, Integer obj) {
            writer.write(obj);
        }
        @Override public Integer read(ByteReader reader) throws InvalidDataException {
            return reader.readInt();
        }
    };

    public static final DataCodec<Long> CODEC_LONG = new DataCodec<Long>() {
        @Override public void write(ByteWriter writer, Long obj) {
            writer.write(obj);
        }
        @Override public Long read(ByteReader reader) throws InvalidDataException {
            return reader.readLong();
        }
    };

    public static final DataCodec<Float> CODEC_FLOAT = new DataCodec<Float>() {
        @Override public void write(ByteWriter writer, Float obj) {
            writer.write(obj);
        }
        @Override public Float read(ByteReader reader) throws InvalidDataException {
            return reader.readFloat();
        }
    };

    public static final DataCodec<Double> CODEC_DOUBLE = new DataCodec<Double>() {
        @Override public void write(ByteWriter writer, Double obj) {
            writer.write(obj);
        }
        @Override public Double read(ByteReader reader) throws InvalidDataException {
            return reader.readDouble();
        }
    };

    public static final DataCodec<Character> CODEC_CHAR = new DataCodec<Character>() {
        @Override public void write(ByteWriter writer, Character obj) {
            writer.write(obj);
        }
        @Override public Character read(ByteReader reader) throws InvalidDataException {
            return reader.readChar();
        }
    };

    public static final DataCodec<Date> CODEC_DATE = new DataCodec<Date>() {
        @Override public void write(ByteWriter writer, Date obj) {
            writer.write(obj.getTime());
        }
        @Override public Date read(ByteReader reader) throws InvalidDataException {
            return new Date(reader.readLong());
        }
    };

    public static final DataCodec<UUID> CODEC_UUID = new DataCodec<UUID>() {
        @Override public void write(ByteWriter writer, UUID obj) {
            writer.write(obj.getMostSignificantBits());
            writer.write(obj.getLeastSignificantBits());
        }
        @Override public UUID read(ByteReader reader) throws InvalidDataException {
            return new UUID(reader.readLong(), reader.readLong());
        }
    };

    public static final DataCodec<InetAddress> CODEC_INET_ADDRESS = new DataCodec<InetAddress>() {
        @Override public void write(ByteWriter writer, InetAddress obj) {
            writer.write(obj.getAddress());
        }
        @Override public InetAddress read(ByteReader reader) throws InvalidDataException {
            byte[] addressBuf = reader.readBytes(4);
            try {
                return InetAddress.getByAddress(addressBuf);
            } catch (UnknownHostException e) {
                throw new InvalidDataException(e);
            }
        }
    };

    public static final DataCodec<String> CODEC_STR8 = new DataCodec<String>() {
        @Override public void write(ByteWriter writer, String obj) {
            writer.writeStr8(obj);
        }
        @Override public String read(ByteReader reader) throws InvalidDataException {
            return reader.readStr8().toString();
        }
    };

    public static final DataCodec<String> CODEC_STR16 = new DataCodec<String>() {
        @Override public void write(ByteWriter writer, String obj) {
            writer.writeStr16(obj);
        }
        @Override public String read(ByteReader reader) throws InvalidDataException {
            return reader.readStr16().toString();
        }
    };

    public static final DataCodec<boolean[]> CODEC_ARRAY_BOOL = new VaryingLengthCodec<boolean[]>() {
        @Override public int size(boolean[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, boolean[] obj) {
            for (boolean b : obj) {
                CODEC_BOOL.write(writer, b);
            }
        }
        @Override protected boolean[] read_do(int size, ByteReader reader) throws InvalidDataException {
            boolean[] bools = new boolean[size];
            for (int i = 0; i < size; i++) {
                bools[i] = CODEC_BOOL.read(reader);
            }
            return bools;
        }
    };

    public static final DataCodec<byte[]> CODEC_ARRAY_BYTE = new VaryingLengthCodec<byte[]>() {
        @Override public int size(byte[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, byte[] obj) {
            for (byte b : obj) {
                CODEC_BYTE.write(writer, b);
            }
        }
        @Override protected byte[] read_do(int size, ByteReader reader) throws InvalidDataException {
            byte[] bytes = new byte[size];
            for (int i = 0; i < size; i++) {
                bytes[i] = CODEC_BYTE.read(reader);
            }
            return bytes;
        }
    };

    public static final DataCodec<short[]> CODEC_ARRAY_SHORT = new VaryingLengthCodec<short[]>() {
        @Override public int size(short[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, short[] obj) {
            for (short s : obj) {
                CODEC_SHORT.write(writer, s);
            }
        }
        @Override protected short[] read_do(int size, ByteReader reader) throws InvalidDataException {
            short[] shorts = new short[size];
            for (int i = 0; i < size; i++) {
                shorts[i] = CODEC_SHORT.read(reader);
            }
            return shorts;
        }
    };

    public static final DataCodec<int[]> CODEC_ARRAY_INT = new VaryingLengthCodec<int[]>() {
        @Override public int size(int[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, int[] obj) {
            for (int i : obj) {
                CODEC_INT.write(writer, i);
            }
        }
        @Override protected int[] read_do(int size, ByteReader reader) throws InvalidDataException {
            int[] ints = new int[size];
            for (int i = 0; i < size; i++) {
                ints[i] = CODEC_INT.read(reader);
            }
            return ints;
        }
    };

    public static final DataCodec<long[]> CODEC_ARRAY_LONG = new VaryingLengthCodec<long[]>() {
        @Override public int size(long[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, long[] obj) {
            for (long l : obj) {
                CODEC_LONG.write(writer, l);
            }
        }
        @Override protected long[] read_do(int size, ByteReader reader) throws InvalidDataException {
            long[] longs = new long[size];
            for (int i = 0; i < size; i++) {
                longs[i] = CODEC_LONG.read(reader);
            }
            return longs;
        }
    };

    public static final DataCodec<float[]> CODEC_ARRAY_FLOAT = new VaryingLengthCodec<float[]>() {
        @Override public int size(float[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, float[] obj) {
            for (float f : obj) {
                CODEC_FLOAT.write(writer, f);
            }
        }
        @Override protected float[] read_do(int size, ByteReader reader) throws InvalidDataException {
            float[] floats = new float[size];
            for (int i = 0; i < size; i++) {
                floats[i] = CODEC_FLOAT.read(reader);
            }
            return floats;
        }
    };

    public static final DataCodec<double[]> CODEC_ARRAY_DOUBLE = new VaryingLengthCodec<double[]>() {
        @Override public int size(double[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, double[] obj) {
            for (double d : obj) {
                CODEC_DOUBLE.write(writer, d);
            }
        }
        @Override protected double[] read_do(int size, ByteReader reader) throws InvalidDataException {
            double[] doubles = new double[size];
            for (int i = 0; i < size; i++) {
                doubles[i] = CODEC_DOUBLE.read(reader);
            }
            return doubles;
        }
    };

    public static final DataCodec<char[]> CODEC_ARRAY_CHAR = new VaryingLengthCodec<char[]>() {
        @Override public int size(char[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, char[] obj) {
            for (char c : obj) {
                CODEC_CHAR.write(writer, c);
            }
        }
        @Override protected char[] read_do(int size, ByteReader reader) throws InvalidDataException {
            char[] chars = new char[size];
            for (int i = 0; i < size; i++) {
                chars[i] = CODEC_CHAR.read(reader);
            }
            return chars;
        }
    };

    public static final DataCodec<Date[]> CODEC_ARRAY_DATE = new VaryingLengthCodec<Date[]>() {
        @Override public int size(Date[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, Date[] obj) {
            for (Date date : obj) {
                CODEC_DATE.write(writer, date);
            }
        }
        @Override protected Date[] read_do(int size, ByteReader reader) throws InvalidDataException {
            Date[] dates = new Date[size];
            for (int i = 0; i < size; i++) {
                dates[i] = CODEC_DATE.read(reader);
            }
            return dates;
        }
    };

    public static final DataCodec<UUID[]> CODEC_ARRAY_UUID = new VaryingLengthCodec<UUID[]>() {
        @Override public int size(UUID[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, UUID[] obj) {
            for (UUID uuid : obj) {
                CODEC_UUID.write(writer, uuid);
            }
        }
        @Override protected UUID[] read_do(int size, ByteReader reader) throws InvalidDataException {
            UUID[] uuids = new UUID[size];
            for (int i = 0; i < size; i++) {
                uuids[i] = CODEC_UUID.read(reader);
            }
            return uuids;
        }
    };

    public static final DataCodec<InetAddress[]> CODEC_ARRAY_INET_ADDRESS = new VaryingLengthCodec<InetAddress[]>() {
        @Override public int size(InetAddress[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, InetAddress[] obj) {
            for (InetAddress inetAddress : obj) {
                CODEC_INET_ADDRESS.write(writer, inetAddress);
            }
        }
        @Override protected InetAddress[] read_do(int size, ByteReader reader) throws InvalidDataException {
            InetAddress[] addresses = new InetAddress[size];
            for (int i = 0; i < size; i++) {
                addresses[i] = CODEC_INET_ADDRESS.read(reader);
            }
            return addresses;
        }
    };

    public static final DataCodec<String[]> CODEC_ARRAY_STR8 = new VaryingLengthCodec<String[]>() {
        @Override public int size(String[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, String[] obj) {
            for (String s : obj) {
                CODEC_STR8.write(writer, s);
            }
        }
        @Override protected String[] read_do(int size, ByteReader reader) throws InvalidDataException {
            String[] strings = new String[size];
            for (int i = 0; i < size; i++) {
                strings[i] = CODEC_STR8.read(reader);
            }
            return strings;
        }
    };

    public static final DataCodec<String[]> CODEC_ARRAY_STR16 = new VaryingLengthCodec<String[]>() {
        @Override public int size(String[] obj) {
            return obj.length;
        }
        @Override protected void write_do(ByteWriter writer, String[] obj) {
            for (String s : obj) {
                CODEC_STR16.write(writer, s);
            }
        }
        @Override protected String[] read_do(int size, ByteReader reader) throws InvalidDataException {
            String[] strings = new String[size];
            for (int i = 0; i < size; i++) {
                strings[i] = CODEC_STR16.read(reader);
            }
            return strings;
        }
    };

    public static final DataCodec<List<Boolean>> CODEC_LIST_BOOL = new CodecList<>(CODEC_BOOL);

    public static final DataCodec<List<Byte>> CODEC_LIST_BYTE = new CodecList<>(CODEC_BYTE);

    public static final DataCodec<List<Short>> CODEC_LIST_SHORT = new CodecList<>(CODEC_SHORT);

    public static final DataCodec<List<Integer>> CODEC_LIST_INT = new CodecList<>(CODEC_INT);

    public static final DataCodec<List<Long>> CODEC_LIST_LONG = new CodecList<>(CODEC_LONG);

    public static final DataCodec<List<Float>> CODEC_LIST_FLOAT = new CodecList<>(CODEC_FLOAT);

    public static final DataCodec<List<Double>> CODEC_LIST_DOUBLE = new CodecList<>(CODEC_DOUBLE);

    public static final DataCodec<List<Character>> CODEC_LIST_CHAR = new CodecList<>(CODEC_CHAR);

    public static final DataCodec<List<Date>> CODEC_LIST_DATE = new CodecList<>(CODEC_DATE);

    public static final DataCodec<List<UUID>> CODEC_LIST_UUID = new CodecList<>(CODEC_UUID);

    public static final DataCodec<List<InetAddress>> CODEC_LIST_INET_ADDRESS = new CodecList<>(CODEC_INET_ADDRESS);

    public static final DataCodec<List<String>> CODEC_LIST_STRING_UTF8 = new CodecList<>(CODEC_STR8);

    public static final DataCodec<List<String>> CODEC_LIST_STRING_UTF16 = new CodecList<>(CODEC_STR16);

}
