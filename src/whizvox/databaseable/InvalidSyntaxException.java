package whizvox.databaseable;

public class InvalidSyntaxException extends RuntimeException {

    public InvalidSyntaxException(String msg, String cause) {
        super("Invalid syntax: " + msg + ": " + cause);
    }

}
