package utilities;

import java.util.Hashtable;

public class ColorTable extends Hashtable<Integer, String> {

    private static ColorTable instance;

    private static final String colors = "lightcoral,limegreen,lightskyblue,"
                                        + "khaki,violet,slateblue,"
                                        + "orange,thistle,olivedrab";

    private ColorTable() {
        super();

        String[] list = colors.split(",");

        for (int i = 0; i < 9; i++) {
            put(i + 1, list[i]);
        }
    }

    public static ColorTable getInstance() {
        if (instance == null) {
            synchronized (ColorTable.class) {
                if (instance == null) {
                    instance = new ColorTable();
                }
            }
        }
        return instance;
    }
}
