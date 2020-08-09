package sudoku.gui.buttons;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import sudoku.core.misc.ColorTable;

import static java.lang.String.valueOf;

class ColorButton extends Button {

    private int count;
    private final String color;
    private final String number;

    ColorButton(String color) {
        super();

        this.count = 0;
        this.color = color;

        setPrefSize(45, 30);
        setFont(new Font(20));

        setStyle("-fx-background-color: " + color + ";");

        this.number = valueOf(ColorTable.getInstance().get(color));

        setText(number);
    }

    int getCount() {
        return count;
    }

    String getColor() {
        return color;
    }

    void incrementCount() {
        if (count == 9) {
            throw new IllegalStateException("Count can not be higher than 9");
        }

        count++;

        if (count == 9) {
            setDisable(true);
            setText("");
        }
    }

    void decreaseCount() {
        if (count == 0) {
            throw new IllegalStateException("Count can not be lower than 0");
        }

        count--;

        if (count == 8) {
            setDisable(false);
            setText(number);
        }
    }

    void reset() {
        setDisable(false);
        setText(number);

        count = 0;
    }
}
