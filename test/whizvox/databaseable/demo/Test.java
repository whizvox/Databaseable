package whizvox.databaseable.demo;

import whizvox.databaseable.Database;
import whizvox.databaseable.io.FileIOGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static whizvox.databaseable.codec.StandardCodecs.*;

public class Test {

    public static void main(String[] args) throws Exception {

        Database<UserData> db = new Database<>(5, UserData.class)
                .setColumnNames("id", "username", "pwdHint", "hashedPwd", "pwdSalt")
                .setCodecs(CODEC_UUID, CODEC_STRING_8BIT, CODEC_STRING_16BIT, CODEC_ARRAY_BYTE, CODEC_ARRAY_BYTE)
                .setDefaultSaveFormat()
                .setIOGenerator(new FileIOGenerator(new File("test.dbab")));



    }

}
