package whizvox.databaseable.demo;

import whizvox.databaseable.Database;
import whizvox.databaseable.io.FileIOGenerator;

import java.io.File;
import java.util.Random;
import java.util.UUID;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class Test {

    public static void main(String[] args) throws Exception {

        Database<UserData> db = new Database<>(5, UserData.class)
                .setColumnNames("id", "username", "pwdHint", "hashedPwd", "pwdSalt")
                .setCodecs(CODEC_UUID, CODEC_STRING_8BIT, CODEC_STRING_16BIT, CODEC_ARRAY_BYTE, CODEC_ARRAY_BYTE)
                .setDefaultSaveFormat()
                .setIOGenerator(new FileIOGenerator(new File("test.dbab")));

        byte[] randBytes = new byte[32];
        Random rand = new Random();
        long t1, t2;
        t1 = System.currentTimeMillis();
        for (int i = 0; i < 5000000; i++) {
            rand.nextBytes(randBytes);
            UserData data = new UserData(UUID.randomUUID(), "player" + i, "password hint " + i, randBytes, randBytes);
            db.add(data);
        }
        t2 = System.currentTimeMillis();
        System.out.println("Adding: " + (t2 - t1) + " ms");
        t1 = System.currentTimeMillis();
        db.save();
        t2 = System.currentTimeMillis();
        System.out.println("Saving: " + (t2 - t1) + " ms");

    }

}
