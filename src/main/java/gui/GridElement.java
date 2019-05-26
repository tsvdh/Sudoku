package gui;

import javafx.scene.control.Button;
import utilities.Grid;
import utilities.Square;

class GridElement extends Button {

    private Square square;

    GridElement() {
        this.setPrefSize(40, 40);
        this.setStyle("-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 0.5px;"
                + "-fx-background-color: white");
    }

    void setSquare(Grid grid, Square square) {
        this.square = square;
        grid.addSquare(square);
    }

    Square getSquare() {
        return this.square;
    }
}
