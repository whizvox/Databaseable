package whizvox.databaseable.test;

import java.util.Random;

public class ByteArrayTest {

    public static void main(String[] args) {

        byte[] bytes = new byte[8];
        Random rand = new Random();
        long l = rand.nextInt();
        bytes[0] = (byte) (l >> 56);
        bytes[1] = (byte) (l >> 48);
        bytes[2] = (byte) (l >> 40);
        bytes[3] = (byte) (l >> 32);
        bytes[4] = (byte) (l >> 24);
        bytes[5] = (byte) (l >> 16);
        bytes[6] = (byte) (l >> 8);
        bytes[7] = (byte) l;

        long read = 0;
        read |= ((long) bytes[0] & 0xff) << 56;
        read |= ((long) bytes[1] & 0xff) << 48;
        read |= ((long) bytes[2] & 0xff) << 40;
        read |= ((long) bytes[3] & 0xff) << 32;
        read |= (bytes[4] & 0xff) << 24;
        read |= (bytes[5] & 0xff) << 16;
        read |= (bytes[6] & 0xff) << 8;
        read |= (bytes[7] & 0xff);

        System.out.println(l);
        System.out.println(read);

    }

}
