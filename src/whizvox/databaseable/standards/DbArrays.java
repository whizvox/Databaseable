package whizvox.databaseable.standards;

import whizvox.databaseable.DbArray;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

import static whizvox.databaseable.standards.StandardCodecs.*;

public class DbArrays {

    private DbArrays() {}

    public static DbArray<String> ofStrings(String[] wrap) {
        return new DbArray<>(wrap, CODEC_STRING);
    }

    public static DbArray<String> ofStrings(int size) {
        return ofStrings(new String[size]);
    }

    public static DbArray<Boolean> ofBooleans(Boolean[] wrap) {
        return new DbArray<>(wrap, CODEC_BOOLEAN_STRING);
    }

    public static DbArray<Boolean> ofBooleans(int size) {
        return ofBooleans(new Boolean[size]);
    }

    public static DbArray<Integer> ofIntegers(Integer[] wrap) {
        return new DbArray<>(wrap, CODEC_INT);
    }

    public static DbArray<Integer> ofIntegers(int size) {
        return ofIntegers(new Integer[size]);
    }

    public static DbArray<Long> ofLongs(Long[] wrap) {
        return new DbArray<>(wrap, CODEC_LONG);
    }

    public static DbArray<Long> ofLongs(int size) {
        return ofLongs(new Long[size]);
    }

    public static DbArray<Float> ofFloats(Float[] wrap) {
        return new DbArray<>(wrap, CODEC_FLOAT);
    }

    public static DbArray<Float> ofFloats(int size) {
        return ofFloats(new Float[size]);
    }

    public static DbArray<UUID> ofUuids(UUID[] wrap) {
        return new DbArray<>(wrap, CODEC_UUID_STRING);
    }

    public static DbArray<UUID> ofUuids(int size) {
        return ofUuids(new UUID[size]);
    }

    public static DbArray<Date> ofDates(Date[] wrap) {
        return new DbArray<>(wrap, CODEC_DATE_LONG);
    }

    public static DbArray<Date> ofDates(int size) {
        return ofDates(new Date[size]);
    }

    public static DbArray<InetAddress> ofInetAddresses(InetAddress[] wrap) {
        return new DbArray<>(wrap, CODEC_INET_ADDRESS_STRING);
    }

    public static DbArray<InetAddress> ofInetAddresses(int size) {
        return ofInetAddresses(new InetAddress[size]);
    }

}
