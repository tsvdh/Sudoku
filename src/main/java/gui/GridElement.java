package gui;

import javafx.scene.control.Button;
import utilities.Square;

class GridElement extends Button {

    private Square square;

    final static int size = 40;

    private final String style = "-fx-border-radius: 0px;"
            + "-fx-border-style: solid;"
            + "-fx-border-color: black;"
            + "-fx-border-width: 0.5px;"
            + "-fx-background-color: white;";

    GridElement() {
        this.setPrefSize(size, size);
        this.setStyle(style);
    }

    void setSquare(Square square) {
        this.square = square;
    }

    Square getSquare() {
        return this.square;
    }

    void setColor(String color) {
        this.setStyle(style + "-fx-border-color: " + color + ";");
    }
}
