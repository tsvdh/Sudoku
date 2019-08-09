package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingsHandler {

    private Properties properties;
    private final String jarFilePath = "config/settings.properties";
    private final String filePath = "/" + jarFilePath;

    public SettingsHandler() {
        properties = new Properties();

        try {
            String relativePath = getClass().getResource(filePath).getPath();
            File file = new File(relativePath);

            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                properties.load(stream);
            } else {
                InputStream stream = getClass().getClassLoader().getResourceAsStream(jarFilePath);
                properties.load(stream);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateFile() {

        try {
            File file = new File(getClass().getResource(filePath).getPath());
            FileOutputStream stream = new FileOutputStream(file);
            properties.store(stream, null);

        } catch (IOException e) {
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
