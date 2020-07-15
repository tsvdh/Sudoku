package gui;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import utils.Square;

import java.util.Observable;
import java.util.Observer;

class GridElement extends GridPane implements Observer {

    private Square square;
    private String borderColor;
    private String backgroundColor;
    private String previousBorderColor;

    final static int size = 60;

    GridElement(Square square) {
        this.setPrefSize(size, size);
        this.borderColor = "black";
        this.setBorderColor("black");
        this.setBackgroundColor("white");

        setSquare(square);
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    private void setSquare(Square square) {
        this.square = square;
        this.square.addObserver(this);
    }

    Square getSquare() {
        return this.square;
    }

    void setBorderColor(String color) {
        previousBorderColor = this.borderColor;
        this.borderColor = color;
        applyStyle();
    }

    void revertBorderColor() {
        if (!borderColor.equals(previousBorderColor)) {
            borderColor = previousBorderColor;
            applyStyle();
        }
    }

    void setBackgroundColor(String color) {
        this.backgroundColor = color;
        applyStyle();
    }

    private void applyStyle() {
        float width = 1.0f;
        if (borderColor.equals("red") || borderColor.equals("lightgreen")) {
            width = 2.0f;
        }
        this.setStyle("-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: " + borderColor + ";"
                + "-fx-border-width: " + width + "px;"
                + "-fx-background-color:" + backgroundColor + ";");
    }

    private void showData(boolean showOptions) {
        this.getChildren().clear();
        this.setAlignment(Pos.CENTER);

        if (square.hasValue()) {

            Label label = new Label(square.getValue().toString());
            label.setFont(new Font(38));

            if (showOptions) {
                label.setStyle("-fx-text-fill: blue");
            }

            this.add(label, 0, 0);
        } else if (showOptions) {
            Integer number = 1;

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {

                    Label label = new Label(number.toString());
                    label.setFont(new Font(13));
                    label.setStyle("-fx-text-fill: blue");

                    if (x == 0) {
                        label.setPadding(new Insets(0, 10, 0, 0));
                    }
                    if (x == 2) {
                        label.setPadding(new Insets(0, 0, 0, 10));
                    }

                    this.add(label, x, y);

                    if (square.getOptions() != null && !square.getOptions().contains(number)) {
                        label.setVisible(false);
                    }

                    number++;
                }
            }
        }
    }

    @Override
    public void update(@Nullable Observable o, Object arg) {
        Platform.runLater(() -> showData( (Boolean) arg));
    }
}
