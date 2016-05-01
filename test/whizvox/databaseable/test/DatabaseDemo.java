package whizvox.databaseable.test;

import whizvox.databaseable.Database;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class DatabaseDemo {

    public static void main(String[] args) throws IOException {

        Database<UserData> db = new Database<>(6, UserData.class)
                .names("id", "name", "accountCreation", "lastOnline", "passwordHash", "passwordSalt")
                .codecs(CODEC_UUID, CODEC_STR16, CODEC_DATE, CODEC_DATE, CODEC_ARRAY_BYTE, CODEC_ARRAY_BYTE)
                .keyColumn(0)
                .file(new File("test.dbab"))
                .defaultSaveFormat();

        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }

        long t1, t2;
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            UserData data = new UserData(UUID.randomUUID(), "player" + i, new Date(System.currentTimeMillis() - 10000), new Date(), bytes, bytes);
            db.add(data);
        }
        t2 = System.currentTimeMillis();
        System.out.println("Adding: " + (t2 - t1) + " ms");

        t1 = System.currentTimeMillis();
        db.save();
        t2 = System.currentTimeMillis();
        System.out.println("Writing: " + (t2 - t1) + " ms");

        Database<UserData> db2 = new Database<>(6, UserData.class)
                .names("id", "name", "accountCreation", "lastOnline", "passwordHash", "passwordSalt")
                .codecs(CODEC_UUID, CODEC_STR16, CODEC_DATE, CODEC_DATE, CODEC_ARRAY_BYTE, CODEC_ARRAY_BYTE)
                .keyColumn(0)
                .file(new File("test.dbab"))
                .defaultSaveFormat();

        t1 = System.currentTimeMillis();
        db2.load();
        t2 = System.currentTimeMillis();
        System.out.println("Reading: " + (t2 - t1) + " ms");

        for (int i = 0; i < db2.getRowCount(); i++) {
            System.out.println(db2.get(i));
        }

    }

}
