package utilities;

import java.io.IOException;
import java.util.Properties;

public class SettingsHandler {

    private Properties properties;

    private void getFromFile() {
        properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().
                    getResourceAsStream("settings.properties"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Integer getDelay() {
        getFromFile();

        String string = properties.getProperty("delay");
        return new Integer(string);
    }

    public String getMode() {
        getFromFile();

        return properties.getProperty("mode");
    }
}
