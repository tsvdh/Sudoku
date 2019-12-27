package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ButtonFactory {

    private static final String colors = "";

    static Button makeButton(String text) {
        Button button = new Button();
        button.setFont(new Font(20));
        button.setPrefSize(100, 30);
        button.setText(text);
        return button;
    }

    static List<ColorButton> makeColorButtons() {
        ArrayList<ColorButton> list = new ArrayList<>(9);

        List<String> colorArray = Arrays.asList(colors.split(","));

        for (int i = 0; i < 9; i++) {
            ColorButton button = new ColorButton(colorArray.get(i), i + 1);
            list.add(button);
        }

        return list;
    }
}
