import gui.main.Sudoku;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        new Sudoku(primaryStage);
    }
}
