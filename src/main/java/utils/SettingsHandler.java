package utils;

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

    private Properties properties;
    private final String filePath = "/config/settings.properties";
    private final String jarFilePath = "/settings.properties";

    public SettingsHandler() {
        properties = new Properties();
        fileIO("read");
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
                    writer.write("interval=40\n");
                    writer.write("pause=2000\n");
                    writer.write("speed=quick\n");
                    writer.write("mode=normal");
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

    public String getMode() {
        return properties.getProperty("mode");
    }

    public Integer getPause() {
        String string = properties.getProperty("pause");
        return new Integer(string);
    }

    public String getSpeed() {
        return properties.getProperty("speed");
    }

    public void setMode(String mode) {
        properties.setProperty("mode", mode);
    }

    public void setInterval(Integer interval) {
        properties.setProperty("interval", interval.toString());
    }

    public void setPause(Integer pause) {
        properties.setProperty("pause", pause.toString());
    }

    public void setSpeed(String speed) {
        properties.setProperty("speed", speed);
    }
}
