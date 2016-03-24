package whizvox.databaseable.demo;

import whizvox.databaseable.Row;

import java.util.Date;
import java.util.UUID;

public class PlayerData extends Row {

    public PlayerData() {}

    public PlayerData(UUID uuid, String name, int heatlh, float armor, float xp, Date loggedIn) {
        setObjects(uuid, name, heatlh, armor, xp, loggedIn);
    }

    public UUID getId() {
        return (UUID) getObject(0);
    }

    public String getName() {
        return (String) getObject(1);
    }

    public int getHealth() {
        return (Integer) getObject(2);
    }

    public float getArmor() {
        return (Float) getObject(3);
    }

    public float getXP() {
        return (Float) getObject(4);
    }

    public Date getLoggedIn() {
        return (Date) getObject(5);
    }

}
