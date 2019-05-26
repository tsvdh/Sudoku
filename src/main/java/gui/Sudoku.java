package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.Grid;
import utilities.Square;

public class Sudoku extends Application {

    private Grid grid;

    public Grid getGrid() {
        return this.grid;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(50));
        gridPane.setStyle("-fx-background-color: white;");
        Platform.runLater(gridPane :: requestFocus);

        buildGrid(gridPane);

        Scene scene = new Scene(gridPane);

        stage.setScene(scene);
        stage.setTitle("Sudoku");
        stage.show();
    }

    private void buildGrid(GridPane gridPane) {
        this.grid = new Grid();

        for (int y = 1; y <= 17; y += 2) {
            for (int x = 1; x <= 17; x += 2) {
                Square square = new Square(null, x / 2 + 1, y / 2 + 1);
                grid.addSquare(square);

                GridElement gridElement = new GridElement();
                gridElement.setSquare(square);

                gridPane.add(gridElement, x , y);
            }
        }

        addLines(gridPane);
    }

    private void addLines(GridPane gridPane) {
        String style = "-fx-border-radius: 0px;"
                + "-fx-border-style: solid;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 0.5px;";

        for (int y = 1; y <= 17; y += 2) {
            for (int x = 0; x <= 18; x += 6) {
                Button verticalLine = new Button();
                verticalLine.setPrefSize(0, GridElement.size);
                verticalLine.setStyle(style);
                verticalLine.setFont(new Font(0));

                gridPane.add(verticalLine, x, y);
            }
        }
        for (int x = 1; x <= 17; x += 2) {
            for (int y = 0; y <= 18; y += 6) {
                Button horizontalLine = new Button();
                horizontalLine.setPrefSize(GridElement.size, 1);
                horizontalLine.setStyle(style);
                horizontalLine.setFont(new Font(0));

                gridPane.add(horizontalLine, x, y);
            }
        }
    }
}
