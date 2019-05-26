package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import utilities.Square;

class GridElement extends GridPane {

    private Square square;

    final static int size = 60;

    GridElement() {
        this.setPrefSize(size, size);
        this.setStyleAndColor("black");
    }

    void setSquare(Square square) {
        this.square = square;
    }

    Square getSquare() {
        return this.square;
    }

    void setStyleAndColor(String color) {
        double width = 0.5;
        if (!color.equals("black")) {
            width = 2.0;
        }

        this.setStyle("-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: " + color + ";"
                + "-fx-border-width: " + width + "px;"
                + "-fx-background-color: white;");
    }

    void showData(boolean showOptions) {
        this.getChildren().clear();

        if (square.getValue() != null) {
            this.add(new Label(square.getValue().toString()), 0, 0);
        } else if (showOptions) {
            Integer number = 1;

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {

                    Label label = new Label(number.toString());
                    this.add(label, x, y);

                    if (!square.getOptions().contains(number)) {
                        label.setVisible(false);
                    }
                }
            }
        }
    }
}
