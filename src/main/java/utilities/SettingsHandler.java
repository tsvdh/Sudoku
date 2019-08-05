package utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsHandler {

    private Properties properties;

    public SettingsHandler() {
        properties = new Properties();

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/settings.properties");
            properties.load(fileInputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateFile() {

        try {
            FileOutputStream stream = new FileOutputStream("src/main/resources/settings.properties");
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
