package whizvox.databaseable_old.standards;

import whizvox.databaseable_old.IOGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class URLIOGenerator extends IOGenerator<URL> {

    public URLIOGenerator(URL seed) {
        super(seed);
    }

    @Override public OutputStream generateOutputStream() throws IOException {
        return getSeed().openConnection().getOutputStream();
    }

    @Override public InputStream generateInputStream() throws IOException {
        return getSeed().openStream();
    }

}
