package whizvox.databaseable_old.standards;

import whizvox.databaseable_old.IOGenerator;

import java.io.*;

public final class FileIOGenerator extends IOGenerator<File> {

    public FileIOGenerator(File seed) {
        super(seed);
    }

    @Override public OutputStream generateOutputStream() throws IOException {
        return new FileOutputStream(getSeed());
    }

    @Override public InputStream generateInputStream() throws IOException {
        return new FileInputStream(getSeed());
    }

}
