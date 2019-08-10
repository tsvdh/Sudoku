package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class SettingsHandler {

    private Properties properties;
    private final String filePath = "/config/settings.properties";
    private final String jarFilePath = "./settings.properties";

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
                file = new File(jarFilePath);
            }

            if (mode.equals("read")) {
                InputStream stream = new FileInputStream(file);
                properties.load(stream);
            }
            if (mode.equals("write")) {
                OutputStream stream = new FileOutputStream(file);
                properties.store(stream, null);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Settings file not found, please make sure it is in the same folder as the JAR.");
            System.exit(0);
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

    public void setMode(String mode) {
        properties.setProperty("mode", mode);
    }

    public void setInterval(Integer interval) {
        properties.setProperty("interval", interval.toString());
    }

    public void setPause(Integer pause) {
        properties.setProperty("pause", pause.toString());
    }
}
