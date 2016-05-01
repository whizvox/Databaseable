package whizvox.databaseable.test;

import whizvox.databaseable.Row;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class UserData extends Row {

    public UserData() {
        super(6);
    }

    public UserData(UUID id, String name, Date accountCreation, Date lastOnline, byte[] passwordHash, byte[] passwordSalt) {
        super(id, name, accountCreation, lastOnline, passwordHash, passwordSalt);
    }

    public UUID getId() {
        return (UUID) get(0);
    }

    public String getName() {
        return (String) get(1);
    }

    public Date getAccountCreation() {
        return (Date) get(2);
    }

    public Date getLastOnline() {
        return (Date) get(3);
    }

    public byte[] getPasswordHash() {
        return (byte[]) get(4);
    }

    public byte[] getPasswordSalt() {
        return (byte[]) get(5);
    }

    @Override
    public String toString() {
        return "UserData(" + getId() + "," + getName() + "," + getAccountCreation() + "," + getLastOnline() + "," + Arrays.toString(getPasswordHash()) + "," + Arrays.toString(getPasswordSalt()) + ")";
    }
}
