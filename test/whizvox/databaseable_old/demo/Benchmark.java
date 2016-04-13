package whizvox.databaseable_old.demo;

import whizvox.databaseable_old.Database;
import whizvox.databaseable_old.standards.FileIOGenerator;

import java.io.File;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static whizvox.databaseable_old.standards.StandardCodecs.*;

public class Benchmark {

    public static void main(String[] args) throws Exception {

        Database<PlayerData> db = new Database<>(6, PlayerData.class)
                .setCodecs(CODEC_UUID_STRING, CODEC_STRING, CODEC_INT, CODEC_FLOAT, CODEC_FLOAT, CODEC_DATE_LONG)
                .setNames("id", "name", "health", "armor", "xp", "loggedIn")
                .setSource(new FileIOGenerator(new File("test.txt")));

        int testCount = 20, iterations = 1000000;
        long total_adding = 0, total_writing = 0, total_reading = 0, delta, t1, t2;

        for (int z = 0; z < testCount; z++) {

            t1 = System.currentTimeMillis();
            Random rand = new Random();
            for (int i = 0; i < iterations; i++) {
                db.add(new PlayerData(UUID.randomUUID(), ";;player" + i, rand.nextInt(101), rand.nextFloat() * 100, rand.nextFloat() * 10, new Date(System.currentTimeMillis())));
            }
            t2 = System.currentTimeMillis();
            delta = t2 - t1;
            total_adding += delta;
            System.out.println("Added " + iterations + " rows in memory in " + delta + " ms");

            t1 = System.currentTimeMillis();
            db.save();
            t2 = System.currentTimeMillis();
            delta = t2 - t1;
            total_writing += delta;
            System.out.println("Wrote " + iterations + " rows to file in " + delta + " ms");

            t1 = System.currentTimeMillis();
            db.load();
            t2 = System.currentTimeMillis();
            delta = t2 - t1;
            total_reading += delta;
            System.out.println("Read " + iterations + " rows from file in " + delta + " ms");

            db.clear();
        }

        long total = total_adding + total_writing + total_reading;
        System.out.println("-----------------");
        System.out.println(" TEST RESULTS");
        System.out.println("-----------------");
        System.out.println();
        System.out.println("Tests Ran: " + testCount);
        System.out.println();
        System.out.println("Total time taken: " + total + " ms");
        System.out.println("  Average: " + formatDecimal(total / (double) testCount) + " ms");
        System.out.println();
        System.out.println("Adding: " + total_adding + " (" + formatDecimal(total_adding * 100.0 / total) + "%)");
        System.out.println("  Average: " + formatDecimal(total_adding / (double) testCount) + " ms");
        System.out.println();
        System.out.println("Writing: " + total_writing + " (" + formatDecimal(total_writing * 100.0 / total) + "%)");
        System.out.println("  Average: " + formatDecimal(total_writing / (double) testCount) + " ms");
        System.out.println();
        System.out.println("Reading: " + total_reading + " (" + formatDecimal(total_reading * 100.0 / total) + "%)");
        System.out.println("  Average: " + formatDecimal(total_reading / (double) testCount) + " ms");
        System.out.println();

    }

    static String formatDecimal(double d) {
        return String.format("%.02f", d);
    }

}
