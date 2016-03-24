package whizvox.databaseable.demo;

import whizvox.databaseable.Database;
import whizvox.databaseable.standards.FileIOGenerator;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static whizvox.databaseable.standards.StandardCodecs.*;

public class Demo {

    public static void main(String[] args) throws Exception {

        Database<PlayerData> db = new Database<>(6, PlayerData.class)
                .setCodecs(CODEC_UUID_STRING, CODEC_STRING, CODEC_INT, CODEC_FLOAT, CODEC_FLOAT, CODEC_DATE_LONG)
                .setNames("id", "name", "health", "armor", "xp", "loggedIn")
                .setSource(new FileIOGenerator(new File("test.txt")));

        int num = 1000000;
        long t1 = System.currentTimeMillis();
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            db.addRow(new PlayerData(UUID.randomUUID(), ";;player" + i, rand.nextInt(101), rand.nextFloat() * 100, rand.nextFloat() * 10, new Date(System.currentTimeMillis())));
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Added " + num + " rows in memory in " + (t2 - t1) + " ms");

        t1 = System.currentTimeMillis();
        db.save();
        t2 = System.currentTimeMillis();
        System.out.println("Wrote " + num + " rows to file in " + (t2 - t1) + " ms");

        t1 = System.currentTimeMillis();
        db.load();
        t2 = System.currentTimeMillis();
        System.out.println("Read " + num + " rows from file in " + (t2 - t1) + " ms");

    }

}
