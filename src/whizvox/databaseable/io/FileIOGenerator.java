package whizvox.databaseable.io;

import java.io.*;

public final class FileIOGenerator extends IOGenerator<File> {

    public FileIOGenerator(File seed) {
        super(seed);
    }

    public FileIOGenerator(String filePath) {
        this(new File(filePath));
    }

    @Override public OutputStream generateOutputStream() throws IOException {
        return new FileOutputStream(getSeed());
    }

    @Override public InputStream generateInputStream() throws IOException {
        return new FileInputStream(getSeed());
    }

}
