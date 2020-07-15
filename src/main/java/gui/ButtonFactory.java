package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import utils.ColorTable;

import java.util.ArrayList;
import java.util.List;

class ButtonFactory {

    static Button makeButton(String text) {
        Button button = new Button();
        button.setFont(new Font(20));
        button.setPrefSize(100, 30);
        button.setText(text);
        return button;
    }

    static List<ColorButton> makeColorButtons() {
        ArrayList<ColorButton> list = new ArrayList<>(9);
        String[] colors = ColorTable.getColors();

        for (int i = 0; i < 9; i++) {
            list.add(i, new ColorButton(colors[i]));
        }

        return list;
    }
}
