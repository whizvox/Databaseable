package whizvox.databaseable_old.standards;

import whizvox.databaseable_old.Codec;
import whizvox.databaseable_old.InvalidSyntaxException;
import whizvox.databaseable_old.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StandardCodecs {

    private StandardCodecs() {}

    public static final Codec<String> CODEC_STRING = new Codec<String>() {
        @Override public Class<String> getObjectClass() {
            return String.class;
        }
        @Override public String write(String obj) {
            return obj;
        }
        @Override public String read(String s) {
            return s;
        }
        @Override public boolean requiresSanitation() {
            return true;
        }
    };

    public static final Codec<String> CODEC_STRING_UNCHECKED = new Codec<String>() {
        @Override public Class<String> getObjectClass() {
            return String.class;
        }
        @Override public String write(String obj) {
            return obj;
        }
        @Override public String read(String s) {
            return s;
        }
    };

    public static final Codec<Boolean> CODEC_BOOLEAN_STRING = new Codec<Boolean>() {
        @Override public Class<Boolean> getObjectClass() {
            return Boolean.class;
        }
        @Override public String write(Boolean obj) {
            return StringUtils.booleanToString(obj);
        }
        @Override public Boolean read(String s) {
            return StringUtils.parseBoolean(s);
        }
    };

    public static final Codec<Boolean> CODEC_BOOLEAN_BIT = new Codec<Boolean>() {
        @Override public Class<Boolean> getObjectClass() {
            return Boolean.class;
        }
        @Override public String write(Boolean obj) {
            return StringUtils.booleanToBitString(obj);
        }
        @Override public Boolean read(String s) {
            return StringUtils.parseBoolean(s);
        }
    };

    public static final Codec<Byte> CODEC_BYTE = new Codec<Byte>() {
        @Override public Class<Byte> getObjectClass() {
            return Byte.class;
        }
        @Override public String write(Byte obj) {
            return Byte.toString(obj);
        }
        @Override public Byte read(String s) {
            return Byte.parseByte(s);
        }
    };

    public static final Codec<Byte> CODEC_BYTE_HEX = new Codec<Byte>() {
        @Override public Class<Byte> getObjectClass() {
            return Byte.class;
        }
        @Override public String write(Byte obj) {
            return Integer.toHexString(obj);
        }
        @Override public Byte read(String s) {
            return Byte.parseByte(s, 16);
        }
    };

    public static final Codec<Integer> CODEC_INT = new Codec<Integer>() {
        @Override public Class<Integer> getObjectClass() {
            return Integer.class;
        }
        @Override public String write(Integer obj) {
            return Integer.toString(obj);
        }
        @Override public Integer read(String s) {
            return Integer.parseInt(s);
        }
    };

    public static final Codec<Integer> CODEC_INT_HEX = new Codec<Integer>() {
        @Override public Class<Integer> getObjectClass() {
            return Integer.class;
        }
        @Override public String write(Integer obj) {
            return Integer.toHexString(obj);
        }
        @Override public Integer read(String s) {
            return Integer.parseInt(s, 16);
        }
    };

    public static final Codec<Long> CODEC_LONG = new Codec<Long>() {
        @Override public Class<Long> getObjectClass() {
            return Long.class;
        }
        @Override public String write(Long obj) {
            return Long.toString(obj);
        }
        @Override public Long read(String s) {
            return Long.parseLong(s);
        }
    };

    public static final Codec<Long> CODEC_LONG_HEX = new Codec<Long>() {
        @Override public Class<Long> getObjectClass() {
            return Long.class;
        }
        @Override public String write(Long obj) {
            return String.format("%016x", obj);
        }
        @Override public Long read(String s) {
            return Long.parseLong(s, 16);
        }
    };

    public static final Codec<Float> CODEC_FLOAT = new Codec<Float>() {
        @Override public Class<Float> getObjectClass() {
            return Float.class;
        }
        @Override public String write(Float obj) {
            return Float.toString(obj);
        }
        @Override public Float read(String s) {
            return Float.parseFloat(s);
        }
    };

    public static final Codec<byte[]> CODEC_BYTE_ARRAY = new Codec<byte[]>() {
        @Override public Class<byte[]> getObjectClass() {
            return byte[].class;
        }
        @Override public String write(byte[] obj) {
            return StringUtils.bytesToHexString(obj);
        }
        @Override public byte[] read(String s) {
            return StringUtils.hexStringToBytes(s);
        }
    };

    public static final Codec<UUID> CODEC_UUID_STRING = new Codec<UUID>() {
        @Override public Class<UUID> getObjectClass() {
            return UUID.class;
        }
        @Override public String write(UUID obj) {
            return obj.toString();
        }
        @Override public UUID read(String s) {
            return UUID.fromString(s);
        }
    };

    public static final Codec<UUID> CODEC_UUID_HEX = new Codec<UUID>() {
        @Override public Class<UUID> getObjectClass() {
            return UUID.class;
        }
        @Override public String write(UUID obj) {
            long mostSig, leastSig;
            mostSig = obj.getMostSignificantBits();
            leastSig = obj.getLeastSignificantBits();
            return CODEC_LONG_HEX.write(mostSig) + CODEC_LONG_HEX.write(leastSig);
        }
        @Override public UUID read(String s) {
            assert s.length() == 32;
            long mostSig, leastSig;
            mostSig = CODEC_LONG_HEX.read(s.substring(0, 16));
            leastSig = CODEC_LONG_HEX.read(s.substring(17));
            return new UUID(mostSig, leastSig);
        }
    };

    public static final Codec<Date> CODEC_DATE_LONG = new Codec<Date>() {
        @Override public Class<Date> getObjectClass() {
            return Date.class;
        }
        @Override public String write(Date obj) {
            return CODEC_LONG.write(obj.getTime());
        }
        @Override public Date read(String s) {
            return new Date(CODEC_LONG.read(s));
        }
    };

    public static final Codec<Date> CODEC_DATE_STRING = new Codec<Date>() {
        private final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-kk:mm:ss:SS");
        private final ParsePosition parsePosition = new ParsePosition(0);

        @Override public Class<Date> getObjectClass() {
            return Date.class;
        }
        @Override public String write(Date obj) {
            return format.format(obj);
        }
        @Override public Date read(String s) {
            return format.parse(s, parsePosition);
        }
    };

    public static final Codec<InetAddress> CODEC_INET_ADDRESS_STRING = new Codec<InetAddress>() {
        @Override public Class<InetAddress> getObjectClass() {
            return InetAddress.class;
        }
        @Override public String write(InetAddress obj) {
            return obj.getHostAddress();
        }
        @Override public InetAddress read(String s) {
            try {
                return InetAddress.getByName(s);
            } catch (UnknownHostException e) {
                throw new InvalidSyntaxException(e.getMessage(), s);
            }
        }
    };

    public static final Codec<InetAddress> CODEC_INET_ADDRESS_BYTE_ARRAY = new Codec<InetAddress>() {
        @Override public Class<InetAddress> getObjectClass() {
            return InetAddress.class;
        }
        @Override public String write(InetAddress obj) {
            return CODEC_BYTE_ARRAY.write(obj.getAddress());
        }
        @Override public InetAddress read(String s) {
            try {
                return InetAddress.getByAddress(CODEC_BYTE_ARRAY.read(s));
            } catch (UnknownHostException e) {
                throw new InvalidSyntaxException(e.getMessage(), s);
            }
        }
    };

}
