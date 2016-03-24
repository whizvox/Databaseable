package whizvox.databaseable.standards;

import whizvox.databaseable.IOGenerator;

import java.io.*;

public final class FileIOGenerator extends IOGenerator<File> {

    public FileIOGenerator(File seed) {
        super(seed);
    }

    @Override
    public OutputStream genOutputStream() throws IOException {
        return new FileOutputStream(getSeed());
    }

    @Override
    public InputStream genInputStream() throws IOException {
        return new FileInputStream(getSeed());
    }

}
