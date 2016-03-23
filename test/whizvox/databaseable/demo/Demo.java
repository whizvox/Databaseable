package whizvox.databaseable.demo;

import whizvox.databaseable.Database;
import whizvox.databaseable.DatabaseableUtil;
import whizvox.databaseable.DefaultCodecs;
import whizvox.databaseable.Row;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Demo {

    public static void main(String[] args) throws Exception {

        FileOutputStream file = new FileOutputStream("test.txt");
        Database<Row> db = new Database<>(2, 10000000)
                .setCodecs(DefaultCodecs.CODEC_STRING, DefaultCodecs.CODEC_INT);

        int num = 10000000;
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            db.addRow(new Row("String" + i, i));
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Added " + num + " rows in memory in " + (t2 - t1) + " ms");

        t1 = System.currentTimeMillis();
        db.save(file);
        file.close();
        t2 = System.currentTimeMillis();
        System.out.println("Wrote " + num + " rows to file in " + (t2 - t1) + " ms");

    }

}
