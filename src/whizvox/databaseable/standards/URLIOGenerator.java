package whizvox.databaseable.standards;

import whizvox.databaseable.IOGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class URLIOGenerator extends IOGenerator<URL> {

    public URLIOGenerator(URL seed) {
        super(seed);
    }

    @Override
    public OutputStream genOutputStream() throws IOException {
        return getSeed().openConnection().getOutputStream();
    }

    @Override
    public InputStream genInputStream() throws IOException {
        return getSeed().openStream();
    }

}
