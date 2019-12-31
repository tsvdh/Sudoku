package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

class ColorButton extends Button {

    private String color;
    private int index;

    ColorButton(String color, int index) {
        super();
        this.color = color;
        this.index = index;

        setPrefSize(45, 30);
        setFont(new Font(20));

        setStyle("-fx-background-color: " + color + ";");
    }

    public String getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }
}
