package common;

import common.options.BuildVersion;
import common.options.InputMethod;
import common.options.Mode;
import common.options.Speed;
import common.popups.Message;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingsHandler {

    private static SettingsHandler instance;

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

    private Properties properties;
    private boolean canWrite;

    private static final String FILE_NAME = "settings.properties";
    private static final String INTERNAL_FILE_PATH = "/configs/" + FILE_NAME;

    private Mode oldMode;

    private SettingsHandler() {
        properties = new Properties();
        canWrite = true;
        fileIO("read");
        oldMode = getMode();
    }

    public void updateFile() {
        fileIO("write");
    }

    private void addMissing() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(INTERNAL_FILE_PATH);
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
                String relativePath = getClass().getResource(INTERNAL_FILE_PATH).getPath();
                file = new File(relativePath);
            }
            else if (protocol.equals("jar")) {
                File tempFile = FileHandler.getLocalFile(FILE_NAME);

                if (!tempFile.exists()) {
                    InputStream inputStream = getClass().getResourceAsStream(INTERNAL_FILE_PATH);
                    FileHandler.writeToFile(inputStream, tempFile);
                }
                if (tempFile.exists()) {
                    file = tempFile;
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
                    InputStream stream = getClass().getResourceAsStream(INTERNAL_FILE_PATH);
                    properties.load(stream);
                    stream.close();
                }

                setCanWrite(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCanWrite(boolean canWrite) {
        if (this.canWrite && !canWrite) {
            new Message("Could not create settings file, make sure the program has correct access rights.\n"
                    + "All changes in settings will not be saved when the application is closed.", true);
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
        String inputMethod = properties.getProperty("input_method");
        return InputMethod.valueOf(inputMethod);
    }

    public boolean getConfirmations() {
        String confirmations = properties.getProperty("confirmations");
        return Boolean.parseBoolean(confirmations);
    }

    public BuildVersion getBuildVersion() {
        String buildVersion = properties.getProperty("build_version");
        return BuildVersion.valueOf(buildVersion);
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
        properties.setProperty("input_method", inputMethod.toString());
    }

    public void setConfirmations(boolean confirmations) {
        properties.setProperty("confirmations", String.valueOf(confirmations));
    }

    public void setBuildVersion(BuildVersion buildVersion) {
        properties.setProperty("build_version", buildVersion.name());
    }
}
