package gui;

import javafx.scene.control.Button;

class ColorButton extends Button {

    private String color;
    private int index;

    ColorButton(String color, int index) {
        super();
        setPrefSize(20, 20);


        setStyle("-fx-background-color: " + color + ";");
    }

    public String getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }
}
