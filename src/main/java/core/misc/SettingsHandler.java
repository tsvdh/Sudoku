package core.misc;

import core.misc.options.InputMethod;
import core.misc.options.Mode;
import core.misc.options.Speed;
import gui.popups.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;

public class SettingsHandler {

    private static SettingsHandler instance;

    private Properties properties;
    private boolean canWrite;

    private final String fileName = "settings.properties";
    private final String internalFilePath = "/config/settings.properties";
    private final String externalPath = "Sudoku solver/";

    private Mode oldMode;

    private SettingsHandler() {
        properties = new Properties();
        canWrite = true;
        fileIO("read");
        oldMode = getMode();
    }

    public static SettingsHandler getInstance() {
        if (instance == null) {
            synchronized (SettingsHandler.class) {
                if (instance == null) {
                    instance = new SettingsHandler();
                }
            }
        }
        return instance;
    }

    public void updateFile() {
        fileIO("write");
    }

    private File createLocalFile() throws URISyntaxException {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String joinedPath = new File(path).getParent() + "/" + fileName;
        return new File(joinedPath);
    }

    private File createExternalFile() {
        String userHome = System.getProperty("user.home");
        String externalPath = new File(userHome).toURI().getPath() + this.externalPath;

        File file = new File(externalPath);
        // If the folder does not exist, try to create it.
        if (!file.exists() && !file.mkdirs()) {
            return file;
        }
        else {
            return new File(externalPath + fileName);
        }
    }

    private Optional<File> getFile(boolean local) throws IOException, URISyntaxException {
        File file;
        boolean canWrite = true;

        if (local) {
            file = createLocalFile();
        }
        else {
            file = createExternalFile();

            if (file.isDirectory()) {
                canWrite = false;
            }
        }

        if (file.createNewFile()) {
            InputStream inputStream = getClass().getResourceAsStream(internalFilePath);
            OutputStream outputStream = new FileOutputStream(file);

            int buffer = inputStream.read();

            while (buffer != -1) {
                outputStream.write(buffer);
                buffer = inputStream.read();
            }

            inputStream.close();
            outputStream.close();
        }
        if (!file.exists()) {
            canWrite = false;
        }

        if (canWrite) {
            return Optional.of(file);
        }
        else {
            return Optional.empty();
        }
    }

    private void addMissing() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(internalFilePath);
        Properties completeProperties = new Properties();
        completeProperties.load(inputStream);

        boolean missing = false;

        for (String key : completeProperties.stringPropertyNames()) {
            if (properties.getProperty(key) == null) {
                missing = true;
                properties.setProperty(key, completeProperties.getProperty(key));
            }
        }

        if (missing) {
            fileIO("write");
        }
    }

    private void fileIO(String mode) {
        try {
            File file = null;
            String protocol = getClass().getResource("").getProtocol();

            if (protocol.equals("file")) {
                String relativePath = getClass().getResource(internalFilePath).toURI().getPath();
                file = new File(relativePath);
            }
            else if (protocol.equals("jar")) {
                Optional<File> optionalFile = getFile(false);

                if (optionalFile.isPresent()) {
                    file = optionalFile.get();
                }
            }
            else {
                throw new UnsupportedOperationException("Unknown protocol for getting files.");
            }

            if (file != null) {
                if (mode.equals("read")) {
                    FileReader reader = new FileReader(file);
                    properties.load(reader);
                    reader.close();

                    addMissing();
                }
                if (mode.equals("write")) {
                    FileWriter writer = new FileWriter(file);
                    properties.store(writer, null);
                    writer.close();
                }

                setCanWrite(true);
            }
            else {
                // Ignore 'write' case, as no external file can be created.
                if (mode.equals("read")) {
                    InputStream stream = getClass().getResourceAsStream(internalFilePath);
                    properties.load(stream);
                    stream.close();
                }

                setCanWrite(false);
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setCanWrite(boolean canWrite) {
        if (this.canWrite && !canWrite) {
            new Message("Could not create settings file, make sure the program has correct access rights.\n"
                    + "All changes in settings will not be saved when the application is closed.");
        }
        this.canWrite = canWrite;
    }

    public Integer getInterval() {
        String string = properties.getProperty("interval");
        return new Integer(string);
    }

    public Mode getMode() {
        String mode = properties.getProperty("mode");
        return Mode.valueOf(mode);
    }

    public Integer getPause() {
        String string = properties.getProperty("pause");
        return new Integer(string);
    }

    public Speed getSpeed() {
        String speed = properties.getProperty("speed");
        return Speed.valueOf(speed);
    }

    public InputMethod getInputMethod() {
        String inputMethod = properties.getProperty("inputMethod");
        return InputMethod.valueOf(inputMethod);
    }

    public boolean getConfirmations() {
        String confirmations = properties.getProperty("confirmations");
        return Boolean.parseBoolean(confirmations);
    }

    public Mode getOldMode() {
        return oldMode;
    }

    public void setMode(Mode mode) {
        oldMode = getMode();
        properties.setProperty("mode", mode.name());
    }

    public void setInterval(Integer interval) {
        properties.setProperty("interval", interval.toString());
    }

    public void setPause(Integer pause) {
        properties.setProperty("pause", pause.toString());
    }

    public void setSpeed(Speed speed) {
        properties.setProperty("speed", speed.name());
    }

    public void setInputMethod(InputMethod inputMethod) {
        properties.setProperty("inputMethod", inputMethod.toString());
    }

    public void setConfirmations(boolean confirmations) {
        properties.setProperty("confirmations", String.valueOf(confirmations));
    }
}
