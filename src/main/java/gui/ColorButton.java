package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import utilities.ColorTable;

import static java.lang.String.valueOf;

class ColorButton extends Button {

    ColorButton(String color) {
        super();

        setPrefSize(45, 30);
        setFont(new Font(20));

        setStyle("-fx-background-color: " + color + ";");

        String number = valueOf(ColorTable.getInstance().get(color));

        setText(number);
    }
}
