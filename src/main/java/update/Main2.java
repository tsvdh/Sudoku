package update;

import javafx.application.Application;
import javafx.stage.Stage;
import sudoku.gui.main.Sudoku;

public class Main2 extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Launcher.boot(getParameters());

        new Sudoku(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
