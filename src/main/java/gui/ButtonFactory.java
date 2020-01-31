package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import utilities.ColorTable;

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
        ColorTable table = ColorTable.getInstance();

        for (int i = 0; i < 9; i++) {
            list.add(i, new ColorButton(table.get(i + 1)));
        }

        return list;
    }
}
