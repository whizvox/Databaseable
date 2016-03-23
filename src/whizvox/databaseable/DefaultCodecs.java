package whizvox.databaseable;

import java.nio.CharBuffer;
import java.util.Date;
import java.util.UUID;

public class DefaultCodecs {

    private DefaultCodecs() {}

    public static final Codec<String> CODEC_STRING = new Codec<String>() {
        @Override
        public Class<String> getObjectClass() {
            return String.class;
        }
        @Override
        public void write(CharBuffer out, String obj) {
            out.append(obj);
        }
        @Override
        public String read(CharBuffer in) {
            return in.toString();
        }
    };

    public static final Codec<Boolean> CODEC_BOOLEAN = new Codec<Boolean>() {
        @Override
        public Class<Boolean> getObjectClass() {
            return Boolean.class;
        }
        @Override
        public void write(CharBuffer out, Boolean obj) {
            out.append(Boolean.toString(obj));
        }
        @Override
        public Boolean read(CharBuffer in) {
            return DatabaseableUtil.parseBoolean(in.toString());
        }
    };

    public static final Codec<Integer> CODEC_INT = new Codec<Integer>() {
        @Override
        public Class<Integer> getObjectClass() {
            return Integer.class;
        }
        @Override
        public void write(CharBuffer out, Integer obj) {
            out.append(Integer.toString(obj));
        }
        @Override
        public Integer read(CharBuffer in) {
            return Integer.parseInt(in.toString());
        }
    };

    public static final Codec<Long> CODEC_LONG_HEX = new Codec<Long>() {
        @Override
        public Class<Long> getObjectClass() {
            return Long.class;
        }
        @Override
        public void write(CharBuffer out, Long obj) {
            out.append(Long.toHexString(obj));
        }
        @Override
        public Long read(CharBuffer in) {
            return Long.parseLong(in.toString(), 16);
        }
    };

    private static char[] HEX_CHARS = new char[] {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static final Codec<Byte[]> CODEC_BYTE_ARRAY = new Codec<Byte[]>() {
        @Override
        public Class<Byte[]> getObjectClass() {
            return Byte[].class;
        }
        @Override
        public void write(CharBuffer out, Byte[] obj) {
            for (Byte b : obj) {
                out.append(HEX_CHARS[b >> 4]);
                out.append(HEX_CHARS[b & 0xf]);
            }
        }
        @Override
        public Byte[] read(CharBuffer in) {
            Byte[] bytes = new Byte[in.remaining() / 2];
            for (int i = 0; i < bytes.length; i += 2) {
                bytes[i / 2] = (byte) ((Character.digit(in.get(), 16) << 4) | Character.digit(in.get(), 16));
            }
            return bytes;
        }
    };

    public static final Codec<UUID> CODEC_UUID_HEX = new Codec<UUID>() {
        @Override
        public Class<UUID> getObjectClass() {
            return UUID.class;
        }
        @Override
        public void write(CharBuffer out, UUID obj) {
            long mostSig = obj.getMostSignificantBits();
            long leastSig = obj.getLeastSignificantBits();
            CODEC_LONG_HEX.write(out, mostSig);
            CODEC_LONG_HEX.write(out, leastSig);
        }
        @Override
        public UUID read(CharBuffer in) {
            long mostSig = CODEC_LONG_HEX.read(in);
            long leastSig = CODEC_LONG_HEX.read(in);
            return new UUID(mostSig, leastSig);
        }
    };

    public static final Codec<Date> CODEC_DATE_HEX = new Codec<Date>() {
        @Override
        public Class<Date> getObjectClass() {
            return Date.class;
        }
        @Override
        public void write(CharBuffer out, Date obj) {
            CODEC_LONG_HEX.write(out, obj.getTime());
        }
        @Override
        public Date read(CharBuffer in) {
            return new Date(CODEC_LONG_HEX.read(in));
        }
    };

}
