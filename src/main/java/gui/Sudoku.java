package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
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

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Square square = new Square(null, x + 1, y + 1);
                grid.addSquare(square);

                GridElement gridElement = new GridElement();
                gridElement.setSquare(square);

                gridPane.add(gridElement, x , y);
            }
        }
    }
}
