package whizvox.databaseable.test;

import whizvox.databaseable.Database;
import whizvox.databaseable.query.ObjectOutput;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class ParsingTest {

    public static void main(String[] args) {

        Database<UserData> db = new Database<>(6, UserData.class)
                .names("id", "name", "accountCreation", "lastOnline", "passwordHash", "passwordSalt")
                .codecs(CODEC_UUID, CODEC_STR16, CODEC_DATE, CODEC_DATE, CODEC_ARRAY_BYTE, CODEC_ARRAY_BYTE)
                .keyColumn(0);

        ObjectOutput<UserData> results = new ObjectOutput<>();
        results.putAll(db);



    }

}
