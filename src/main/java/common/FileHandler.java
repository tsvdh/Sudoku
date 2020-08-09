package common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHandler {

    public static File getExternalFileInHome(String path) throws IOException {
        String userHome = System.getProperty("user.home");
        File userHomeFile = new File(userHome);
        String externalPath = userHomeFile.toURI().getPath() + path;

        File file = new File(externalPath);
        File parent = file.getParentFile();

        if (userHomeFile.equals(parent)) {
            throw new IllegalArgumentException("Can only put folders in user home");
        }

        // If the folder does not exist, try to create it.
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create " + externalPath);
        }
        else {
            return file;
        }
    }

    public static File getLocalFile(String fileName) {
        String path = FileHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String joinedPath = new File(path).getParent() + "/" + fileName;
        return new File(joinedPath);
    }

    public static void writeToFile(InputStream inputStream, File file) throws IOException {
        OutputStream outputStream;
        try {
             outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        byte[] buffer = new byte[1024];

        int amountRead;
        while ((amountRead = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, amountRead);
        }
    }
}
