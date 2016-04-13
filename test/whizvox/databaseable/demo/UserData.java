package whizvox.databaseable.demo;

import whizvox.databaseable.Row;

import java.util.UUID;

public class UserData extends Row {

    public UserData() {
        super(5);
    }

    public UserData(UUID id, String username, String passwordHint, byte[] hashedPassword, byte[] passwordSalt) {
        super(id, username, passwordHint, hashedPassword, passwordSalt);
    }

    public UUID getId() {
        return (UUID) get(0);
    }

    public String getUsername() {
        return (String) get(1);
    }

    public String getPasswordHint() {
        return (String) get(2);
    }

    public byte[] getHashedPassword() {
        return (byte[]) get(3);
    }

    public byte[] getPasswordSalt() {
        return (byte[]) get(4);
    }

}
