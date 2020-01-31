package gui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

class ColorButton extends Button {

    private String color;

    ColorButton(String color) {
        super();
        this.color = color;

        setPrefSize(45, 30);
        setFont(new Font(20));

        setStyle("-fx-background-color: " + color + ";");
    }

    public String getColor() {
        return color;
    }
}
