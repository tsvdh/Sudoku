package utilities;

import java.io.IOException;
import java.util.Properties;

class SettingsHandler {

    Integer getDelay() {
        Properties properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("settings.properties"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String string = properties.getProperty("delay");
        return new Integer(string);
    }
}
