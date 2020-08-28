package sudoku.gui.buttons;

import javafx.scene.control.Button;
import sudoku.core.misc.ColorTable;

import java.util.ArrayList;
import java.util.List;

public class ButtonFactory {

    public static Button makeBigButton(String text) {
        Button button = new Button();
        button.setPrefSize(105, 30);
        button.setText(text);
        return button;
    }

    public static Button makeSmallButton(String text) {
        Button button = new Button();
        button.setPrefSize(75, 30);
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

    public static Button makeInvisibleButton() {
        Button filler = makeBigButton("Invisible");
        filler.setVisible(false);

        return filler;
    }
}
