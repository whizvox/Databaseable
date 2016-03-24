package whizvox.databaseable.standards;

import whizvox.databaseable.Codec;
import whizvox.databaseable.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StandardCodecs {

    private StandardCodecs() {}

    public static final Codec<String> CODEC_STRING = new Codec<String>() {
        @Override
        public Class<String> getObjectClass() {
            return String.class;
        }
        @Override
        public CharSequence write(String obj) {
            return obj;
        }
        @Override
        public String read(CharSequence s) {
            return s.toString();
        }
        @Override
        public boolean requiresSanitation() {
            return true;
        }
    };

    public static final Codec<Boolean> CODEC_BOOLEAN = new Codec<Boolean>() {
        @Override
        public Class<Boolean> getObjectClass() {
            return Boolean.class;
        }
        @Override
        public CharSequence write(Boolean obj) {
            return Boolean.toString(obj);
        }
        @Override
        public Boolean read(CharSequence s) {
            return StringUtils.parseBoolean(s);
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Integer> CODEC_INT = new Codec<Integer>() {
        @Override
        public Class<Integer> getObjectClass() {
            return Integer.class;
        }
        @Override
        public CharSequence write(Integer obj) {
            return Integer.toString(obj);
        }
        @Override
        public Integer read(CharSequence s) {
            return Integer.parseInt(s.toString());
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Long> CODEC_LONG = new Codec<Long>() {
        @Override
        public Class<Long> getObjectClass() {
            return Long.class;
        }
        @Override
        public CharSequence write(Long obj) {
            return Long.toString(obj);
        }
        @Override
        public Long read(CharSequence s) {
            return Long.parseLong(s.toString());
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Float> CODEC_FLOAT = new Codec<Float>() {
        @Override
        public Class<Float> getObjectClass() {
            return Float.class;
        }
        @Override
        public CharSequence write(Float obj) {
            return Float.toString(obj);
        }
        @Override
        public Float read(CharSequence s) {
            return Float.parseFloat(s.toString());
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Long> CODEC_LONG_HEX = new Codec<Long>() {
        @Override
        public Class<Long> getObjectClass() {
            return Long.class;
        }
        @Override
        public CharSequence write(Long obj) {
            return String.format("%016x", obj);
        }
        @Override
        public Long read(CharSequence s) {
            return Long.parseLong(s.toString(), 16);
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Byte[]> CODEC_BYTE_ARRAY = new Codec<Byte[]>() {
        @Override
        public Class<Byte[]> getObjectClass() {
            return Byte[].class;
        }
        @Override
        public CharSequence write(Byte[] obj) {
            return StringUtils.bytesToString(obj);
        }
        @Override
        public Byte[] read(CharSequence s) {
            return StringUtils.stringToBoxedBytes(s);
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<UUID> CODEC_UUID_HEX = new Codec<UUID>() {
        @Override
        public Class<UUID> getObjectClass() {
            return UUID.class;
        }
        @Override
        public CharSequence write(UUID obj) {
            long mostSig, leastSig;
            mostSig = obj.getMostSignificantBits();
            leastSig = obj.getLeastSignificantBits();
            return CODEC_LONG_HEX.write(mostSig).toString() + CODEC_LONG_HEX.write(leastSig);
        }
        @Override
        public UUID read(CharSequence s) {
            long mostSig, leastSig;
            mostSig = CODEC_LONG_HEX.read(s.subSequence(0, s.length() / 2));
            leastSig = CODEC_LONG_HEX.read(s.subSequence(s.length() / 2 + 1, s.length()));
            return new UUID(mostSig, leastSig);
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<UUID> CODEC_UUID_STRING = new Codec<UUID>() {
        @Override
        public Class<UUID> getObjectClass() {
            return UUID.class;
        }
        @Override
        public CharSequence write(UUID obj) {
            return obj.toString();
        }
        @Override
        public UUID read(CharSequence s) {
            return UUID.fromString(s.toString());
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Date> CODEC_DATE_LONG = new Codec<Date>() {
        @Override
        public Class<Date> getObjectClass() {
            return Date.class;
        }
        @Override
        public CharSequence write(Date obj) {
            return CODEC_LONG.write(obj.getTime());
        }
        @Override
        public Date read(CharSequence s) {
            return new Date(CODEC_LONG.read(s));
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

    public static final Codec<Date> CODEC_DATE_STRING = new Codec<Date>() {
        private final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-kk:mm:ss:SS");
        private final ParsePosition parsePosition = new ParsePosition(0);
        @Override
        public Class<Date> getObjectClass() {
            return Date.class;
        }
        @Override
        public CharSequence write(Date obj) {
            return format.format(obj);
        }
        @Override
        public Date read(CharSequence s) {
            return format.parse(s.toString(), parsePosition);
        }
        @Override
        public boolean requiresSanitation() {
            return false;
        }
    };

}
