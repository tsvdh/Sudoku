package core.misc;

import core.misc.options.InputMethod;
import core.misc.options.Mode;
import core.misc.options.Speed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;

public class SettingsHandler {

    private static SettingsHandler instance;

    private Properties properties;
    private final String filePath = "/config/settings.properties";
    private final String jarFilePath = "/settings.properties";

    private Mode oldMode;

    private SettingsHandler() {
        properties = new Properties();
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

    private void fileIO(String mode) {
        try {
            String relativePath = getClass().getResource(filePath).getPath();
            File file = new File(relativePath);

            if (!file.exists()) {
                String path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                String root = path.substring(0, path.lastIndexOf("/"));
                String joinedPath = root + jarFilePath;
                file = new File(joinedPath);

                if (file.createNewFile()) {
                    FileWriter writer = new FileWriter(file);
                    writer.write("interval=80\n");
                    writer.write("pause=2000\n");
                    writer.write("speed=SLOW\n");
                    writer.write("mode=NORMAL\n");
                    writer.write("inputMethod=LEGACY\n");
                    writer.write("confirmations=true");
                    writer.flush();
                    writer.close();
                }
                if (!file.exists()) {
                    System.out.println("Could not create settings file.");
                    System.exit(0);
                }
            }

            if (mode.equals("read")) {
                InputStream stream = new FileInputStream(file);
                properties.load(stream);
            }
            if (mode.equals("write")) {
                OutputStream stream = new FileOutputStream(file);
                properties.store(stream, null);
            }

        } catch (IOException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
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
