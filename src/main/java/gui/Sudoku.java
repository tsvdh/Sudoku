package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Sudoku extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(50));
        buildGrid(gridPane);
        gridPane.setStyle("-fx-background-color: white;");

        Platform.runLater(gridPane :: requestFocus);

        Scene scene = new Scene(gridPane);

        stage.setScene(scene);
        stage.setTitle("Sudoku");
        stage.show();
    }

    private void buildGrid(GridPane gridPane) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                gridPane.add(new GridElement(), j , i);
            }
        }
    }
}
