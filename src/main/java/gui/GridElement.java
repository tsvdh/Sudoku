package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import utilities.Square;

import java.util.Observable;
import java.util.Observer;

class GridElement extends GridPane implements Observer {

    private Square square;

    final static int size = 60;

    GridElement() {
        this.setPrefSize(size, size);
        this.setStyleAndColor("black");
    }

    void setSquare(Square square) {
        this.square = square;
        this.square.addObserver(this);
    }

    Square getSquare() {
        return this.square;
    }

    void setStyleAndColor(String borderColor) {
        double width = 0.5;
        if (!borderColor.equals("black")) {
            width = 2.0;
        }

        this.setStyle("-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: " + borderColor + ";"
                + "-fx-border-width: " + width + "px;"
                + "-fx-background-color: white;");
    }

    private void showData(boolean showOptions) {
        this.getChildren().clear();
        this.setAlignment(Pos.CENTER);

        if (square.hasValue()) {

            Label label = new Label(square.getValue().toString());
            label.setFont(new Font(38));

            if (showOptions) {
                label.setStyle("-fx-text-fill: darkblue");
            }

            this.add(label, 0, 0);
        } else if (showOptions) {
            Integer number = 1;

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {

                    Label label = new Label(number.toString());
                    label.setFont(new Font(13));
                    label.setStyle("-fx-text-fill: darkblue");

                    if (x == 0) {
                        label.setPadding(new Insets(0, 10, 0, 0));
                    }
                    if (x == 2) {
                        label.setPadding(new Insets(0, 0, 0, 10));
                    }

                    this.add(label, x, y);

                    if (!square.getOptions().contains(number)) {
                        label.setVisible(false);
                    }

                    number++;
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        showData( (Boolean) arg);
    }
}
