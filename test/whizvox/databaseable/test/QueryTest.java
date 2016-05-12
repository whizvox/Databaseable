package whizvox.databaseable.test;

import whizvox.databaseable.Database;
import whizvox.databaseable.query.Command;
import whizvox.databaseable.query.CommandReceiver;
import whizvox.databaseable.query.ObjectOutput;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class QueryTest extends CommandReceiver {

    public static void main(String[] args) throws Exception {
        instance = new QueryTest()
                .defaultCommands()
                .defaultArguments()
                .defaultCodecs()
                .workingRow(UserData.class);
        new Thread(instance).start();
        Database<UserData> db = new Database<>(6, UserData.class)
                .file(new File("test.db"))
                .defaultSaveFormat()
                .codecs(CODEC_UUID, CODEC_STR16, CODEC_DATE, CODEC_DATE, CODEC_ARRAY_BYTE, CODEC_ARRAY_BYTE)
                .names("id", "name", "lastOnline", "accountCreated", "passwordHash", "passwordSalt");
        db.save();
        Random rand = new Random();
        System.out.println("DATABASE CREATED");
        byte[] b1 = new byte[16];
        byte[] b2 = new byte[8];
        for (int i = 0; i < 10000; i++) {
            rand.nextBytes(b1);
            rand.nextBytes(b2);
            db.add(new UserData(UUID.randomUUID(), "player" + i, new Date(System.currentTimeMillis() - rand.nextInt(1000000)), new Date(System.currentTimeMillis() - 10000000 - rand.nextInt(100000)), b1, b2));
        }
        db.save();
        System.out.println("DATABASE FILLED");
    }

    private static CommandReceiver instance;

    protected PrintStream stream() {
        return (PrintStream) getOut();
    }

    @Override
    protected void onExecute(Command cmd, ObjectOutput out) {
        stream().println();
        stream().println("OUTPUT:");
        out.forEach(o -> stream().println(o));
        stream().println();
    }

    @Override
    protected void exception(OutputStream out, Throwable t) {
        PrintStream stream = (PrintStream) out;
        stream.println();
        stream.println("EXCEPTION:");
        stream.println(t);
        for (StackTraceElement e : t.getStackTrace()) {
            stream().println("\t" + e);
        }
        stream.println();
    }

}
