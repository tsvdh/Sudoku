package utilities;

import java.io.IOException;
import java.util.Properties;

public class DynamicLoader {

    public Integer getInterval() {
        Properties properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("settings.properties"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String string = properties.getProperty("interval");
        return new Integer(string);
    }

    public static void main(String[] args) {
        DynamicLoader loader = new DynamicLoader();
        System.out.println(loader.getInterval());
    }
}
