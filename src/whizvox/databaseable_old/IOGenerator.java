package whizvox.databaseable_old;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class IOGenerator<T> {

    private T seed;

    public IOGenerator(T seed) {
        this.seed = seed;
    }

    public final T getSeed() {
        return seed;
    }

    public abstract OutputStream generateOutputStream() throws IOException;

    public abstract InputStream generateInputStream() throws IOException;

}
