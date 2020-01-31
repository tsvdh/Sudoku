package utilities;

import java.util.Hashtable;

public class ColorTable extends Hashtable<String, Integer> {

    private static ColorTable instance;

    private static final String[] colors = {"lightcoral", "limegreen", "lightskyblue",
                                            "khaki" ," violet", "mediumslateblue",
                                            "orange", "thistle", "olivedrab"};

    private ColorTable() {
        super();

        for (int i = 0; i < 9; i++) {
            put(colors[i], i + 1);
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

    public static String[] getColors() {
        return colors;
    }
}
