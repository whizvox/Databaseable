package whizvox.databaseable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SaveFormat {

    void write(OutputStream out, Database database) throws IOException ;

    <T extends Row> void read(InputStream in, Database<T> database) throws IOException;

}
