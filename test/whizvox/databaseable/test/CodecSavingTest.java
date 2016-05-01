package whizvox.databaseable.test;

import whizvox.databaseable.io.ByteReader;
import whizvox.databaseable.io.ByteWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class CodecSavingTest {

    static final int BOUNDS_LOW = 10000, BOUNDS_DELTA = 1000;
    static final String FILE_NAME = "test.txt";

    public static void main(String[] args) throws Exception {

        byte[] bytes = new byte[1024];
        OutputStream out = new FileOutputStream(FILE_NAME);
        ByteWriter writer = new ByteWriter(bytes);
        Random rand = new Random();
        int iterations = rand.nextInt(BOUNDS_DELTA) + BOUNDS_LOW;
        writer.write(iterations);
        writer.writeTo(out);
        for (int i = 0; i < iterations; i++) {
            UUID id = new UUID(rand.nextLong(), rand.nextLong());
            String name = "ゲイマ" + i;
            byte[] hash = new byte[16];
            rand.nextBytes(hash);
            CODEC_UUID.write(writer, id);
            CODEC_STR16.write(writer, name);
            CODEC_ARRAY_BYTE.write(writer, hash);
            writer.writeTo(out);
            writer.reset();
        }
        out.close();

        // simulating starting from a fresh buffer
        Arrays.fill(bytes, (byte) 0);
        ByteReader reader = new ByteReader(bytes);
        FileInputStream in = new FileInputStream(FILE_NAME);
        reader.readFromStream(in);
        int iter = reader.readInt();
        reader.readFromStream(in);
        for (int i = 0; i < iter; i++) {
            UUID id = CODEC_UUID.read(reader);
            String name = CODEC_STR16.read(reader);
            byte[] hash = CODEC_ARRAY_BYTE.read(reader);
            System.out.println(id + ": " + name + " " + Arrays.toString(hash));
            reader.readFromStream(in);
        }

    }

}
